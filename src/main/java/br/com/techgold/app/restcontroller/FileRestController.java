package br.com.techgold.app.restcontroller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.techgold.app.dto.DtoFile;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.Colaborador;
import br.com.techgold.app.model.CustomUserDetails;
import br.com.techgold.app.services.ClienteService;
import br.com.techgold.app.services.ColaboradorService;
import br.com.techgold.app.services.SolicitacaoService;

@RestController
@RequestMapping("/api/file")
public class FileRestController {
	
	@Autowired
	ClienteService service;
	
	@Autowired
	ColaboradorService colaboradorService;
	
	@Autowired
	SolicitacaoService solicitacaoService;
	
	@Value("${upload.dir}")
	private String UPLOAD_DIR;
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@ModelAttribute DtoFile uploadRequest){
		
		MultipartFile file = uploadRequest.file();
		Long id = uploadRequest.id();
		
		if(file.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("o arquivo está vazio");
		}
		
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			File uploadDir = new File(UPLOAD_DIR+"/solicitacoes/"+id+"/");
			
			if(!uploadDir.exists()) {
				boolean created = uploadDir.mkdirs();
				
				if(!created) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possivel criar o diretorio");
				}
			}
			
			File dest = new File(UPLOAD_DIR +"/solicitacoes/"+ id +"/" + fileName);
			file.transferTo(dest);
			
			return ResponseEntity.ok("Arquivo enviado com sucesso!");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo!");
		}
	}
	
	@PostMapping("/perfil/upload")
	public ResponseEntity<String> perfilUploadFile(@ModelAttribute DtoFile uploadRequest) {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();

		Long id = null;
		String tipo = null;
		String caminhoBase = null;

		Object entidade = null;

		if (principal instanceof Cliente cliente) {
		    id = cliente.getId();
		    tipo = "cliente";
		    caminhoBase = "/cliente/";
		    entidade = cliente;
		}

		else if (principal instanceof CustomUserDetails custom) {

		    entidade = custom.getEntidade();

		    if (entidade instanceof Cliente cliente) {
		        id = cliente.getId();
		        tipo = "cliente";
		        caminhoBase = "/cliente/";
		    }

		    else if (entidade instanceof Colaborador colab) {
		        id = colab.getId();
		        tipo = "colaborador";
		        caminhoBase = "/colaborador/";
		    }
		}

		if (id == null) {
		    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário inválido");
		}

	   
	    MultipartFile file = uploadRequest.file();
	    		
	    String contentType = file.getContentType();
	    String originalFileName = file.getOriginalFilename();

	    if (!isValidImage(contentType, originalFileName)) {
	        return ResponseEntity.badRequest().body("Somente arquivos JPEG e PNG são permitidos");
	    }

	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("O arquivo está vazio");
	    }

	    try {
	    	File uploadDir = new File(UPLOAD_DIR + caminhoBase + id + "/");
	        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Não foi possível criar o diretório");
	        }

	        // Limpa arquivos existentes
	        Files.list(uploadDir.toPath())
	            .filter(Files::isRegularFile)
	            .forEach(path -> {
	                try {
	                    Files.delete(path);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            });

	        BufferedImage originalImage = ImageIO.read(file.getInputStream());
	        if (originalImage == null) {
	            return ResponseEntity.badRequest().body("Arquivo inválido.");
	        }

	        // Cria nova imagem RGB com fundo branco (para remover transparência)
	        BufferedImage newImage = new BufferedImage(
	            originalImage.getWidth(),
	            originalImage.getHeight(),
	            BufferedImage.TYPE_INT_RGB
	        );
	        Graphics2D g = newImage.createGraphics();
	        g.setColor(Color.WHITE); // fundo branco
	        g.fillRect(0, 0, originalImage.getWidth(), originalImage.getHeight());
	        g.drawImage(originalImage, 0, 0, null);
	        g.dispose();

	        // Salva como JPEG com compressão
	        String finalFileName = "perfil_" + tipo + "_" + id + ".jpg";
	        Path destino = Paths.get(uploadDir.getAbsolutePath(), finalFileName);

	        try (OutputStream os = Files.newOutputStream(destino)) {
	            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
	            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
	            writer.setOutput(ios);

	            ImageWriteParam param = writer.getDefaultWriteParam();
	            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	            param.setCompressionQuality(0.3f); // 0.0 = baixa qualidade, 1.0 = alta

	            writer.write(null, new IIOImage(newImage, null, null), param);
	            ios.close();
	            writer.dispose();
	        }

	        String caminho = caminhoBase + id + "/" + finalFileName;

	        if (entidade instanceof Cliente cliente) {
	            service.atualizaImagem(cliente.getId(), caminho);
	        }

	        else if (entidade instanceof Colaborador colab) {
	            colaboradorService.atualizaImagem(colab.getId(), caminho);
	        }
	        return ResponseEntity.ok("Arquivo enviado com sucesso!");

	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo!");
	    }
	}

	
	private boolean isValidImage(String contentType, String fileName) {
		return (contentType != null && (
				contentType.equalsIgnoreCase("image/jpeg") ||
				contentType.equalsIgnoreCase("image/png")
				)) &&
				(fileName  != null && (
						fileName.toLowerCase().endsWith(".jpg") ||
						fileName.toLowerCase().endsWith(".jpeg") ||
						fileName.toLowerCase().endsWith(".png")
						));
	}
	
	@GetMapping("/perfil")
	public ResponseEntity<Resource> exibirImagem() {
		
		System.out.println("perfil");

	    var auth = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = auth.getPrincipal();

	    String caminhoFoto = null;

	    // 🔹 Cliente direto
	    if (principal instanceof Cliente cliente) {

	        Cliente clienteAtualizado = service.buscaPorNome(cliente.getNomeCliente());

	        caminhoFoto = clienteAtualizado.getCaminhoFoto();

	        System.out.println("CAMINHO_DB: " + caminhoFoto);
	    }

	    // 🔹 CustomUserDetails
	    else if (principal instanceof CustomUserDetails custom) {
	    	
	        Object entidade = custom.getEntidade();

	        if (entidade instanceof Cliente cliente) {
	            caminhoFoto = cliente.getCaminhoFoto();
	        }

	        else if (entidade instanceof Colaborador colab) {
	            caminhoFoto = colab.getCaminhoFoto();
	        }
	    }

	    if (caminhoFoto == null) {
	        return ResponseEntity.notFound().build();
	    }

	    try {
	        Path base = Paths.get(UPLOAD_DIR);

	        // 🔥 CORREÇÃO IMPORTANTE (sem duplicar path)
	        Path caminhoArquivo = base.resolve(caminhoFoto.replaceFirst("^/", "")).normalize();

	        Resource recurso = new UrlResource(caminhoArquivo.toUri());

	        if (!recurso.exists()) {
	            return ResponseEntity.notFound().build();
	        }

	        String contentType = Files.probeContentType(caminhoArquivo);
	        if (contentType == null) {
	            contentType = "application/octet-stream";
	        }

	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header("Content-Disposition", "inline; filename=\"" + recurso.getFilename() + "\"")
	                .body(recurso);

	    } catch (MalformedURLException e) {
	        return ResponseEntity.badRequest().build();
	    } catch (Exception e) {
	        return ResponseEntity.internalServerError().build();
	    }
	}
	
	
//	@PostMapping("/solicitacao/upload")
//	public ResponseEntity<String> solicitacaoUploadFile(@ModelAttribute DtoFile uploadRequest) {
//
//	    Solicitacao solicitacao = solicitacaoService.buscarPorId(uploadRequest.id());
//
//	    if (!uploadRequest.id().equals(solicitacao.getId())) {
//	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//	                .body("Não autorizado a alterar esta solicitacao.");
//	    }
//
//	    MultipartFile file = uploadRequest.file();
//	    Long id = uploadRequest.id();
//
//	    if (file == null || file.isEmpty()) {
//	        return ResponseEntity.badRequest().body("O arquivo está vazio");
//	    }
//
//	    String contentType = file.getContentType();
//	    String originalFileName = file.getOriginalFilename();
//
//	    if (!isValidUpload(contentType, originalFileName)) {
//	        return ResponseEntity.badRequest().body("Somente arquivos PDF, JPEG e PNG são permitidos");
//	    }
//
//	    try {
//	        File uploadDir = new File(UPLOAD_DIR + "/solicitacoes/" + id + "/");
//	        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
//	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                    .body("Não foi possível criar o diretório");
//	        }
//
//	        // Limpa arquivos existentes
//	        Files.list(uploadDir.toPath())
//	                .filter(Files::isRegularFile)
//	                .forEach(path -> {
//	                    try {
//	                        Files.delete(path);
//	                    } catch (IOException e) {
//	                        e.printStackTrace();
//	                    }
//	                });
//
//	        boolean isPdf = isPdf(contentType, originalFileName);
//
//	        String finalFileName;
//	        Path destino;
//
//	        // ==========================
//	        // PDF -> salva direto
//	        // ==========================
//	        if (isPdf) {
//	            finalFileName = "solicitacao_" + id + ".pdf";
//	            destino = Paths.get(uploadDir.getAbsolutePath(), finalFileName);
//
//	            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
//
//	            solicitacaoService.atualizaArquivo(
//	                    solicitacao.getId(),
//	                    "/solicitacoes/" + id + "/" + finalFileName
//	            );
//
//	            return ResponseEntity.ok("PDF enviado com sucesso!");
//	        }
//
//	        // ==========================
//	        // IMAGEM -> processa e salva como JPG
//	        // ==========================
//	        BufferedImage originalImage = ImageIO.read(file.getInputStream());
//	        if (originalImage == null) {
//	            return ResponseEntity.badRequest().body("Arquivo inválido.");
//	        }
//
//	        // Cria nova imagem RGB com fundo branco (para remover transparência)
//	        BufferedImage newImage = new BufferedImage(
//	                originalImage.getWidth(),
//	                originalImage.getHeight(),
//	                BufferedImage.TYPE_INT_RGB
//	        );
//
//	        Graphics2D g = newImage.createGraphics();
//	        g.setColor(Color.WHITE);
//	        g.fillRect(0, 0, originalImage.getWidth(), originalImage.getHeight());
//	        g.drawImage(originalImage, 0, 0, null);
//	        g.dispose();
//
//	        // Salva como JPEG com compressão
//	        finalFileName = "solicitacao_" + id + ".jpg";
//	        destino = Paths.get(uploadDir.getAbsolutePath(), finalFileName);
//
//	        try (OutputStream os = Files.newOutputStream(destino)) {
//	            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
//	            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//	            writer.setOutput(ios);
//
//	            ImageWriteParam param = writer.getDefaultWriteParam();
//	            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//	            param.setCompressionQuality(0.5f);
//
//	            writer.write(null, new IIOImage(newImage, null, null), param);
//	            ios.close();
//	            writer.dispose();
//	        }
//
//	        solicitacaoService.atualizaArquivo(
//	                solicitacao.getId(),
//	                "/solicitacoes/" + id + "/" + finalFileName
//	        );
//
//	        return ResponseEntity.ok("Imagem enviada com sucesso!");
//
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo!");
//	    }
//	}

	private boolean isJpeg(String contentType, String fileName) {
	    boolean mimeOk = contentType != null && (
	            contentType.equalsIgnoreCase("image/jpeg") ||
	            contentType.equalsIgnoreCase("image/jpg")
	    );
	    boolean extOk = fileName != null && (
	            fileName.toLowerCase().endsWith(".jpg") ||
	            fileName.toLowerCase().endsWith(".jpeg")
	    );
	    return mimeOk || extOk;
	}

	private boolean isPng(String contentType, String fileName) {
	    boolean mimeOk = contentType != null && contentType.equalsIgnoreCase("image/png");
	    boolean extOk = fileName != null && fileName.toLowerCase().endsWith(".png");
	    return mimeOk || extOk;
	}

	private Cliente getClienteLogado() {

	    var auth = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = auth.getPrincipal();

	    // 🔹 Cliente direto
	    if (principal instanceof Cliente c) {
	        return service.buscaPorNome(c.getNomeCliente());
	    }

	    // 🔹 CustomUserDetails
	    if (principal instanceof CustomUserDetails custom) {

	        Object entidade = custom.getEntidade();

	        if (entidade instanceof Cliente c) {
	            return service.buscaPorNome(c.getNomeCliente());
	        }

	        if (entidade instanceof Colaborador colab) {
	            return colab.getCliente();
	        }
	    }

	    throw new RuntimeException("Cliente não encontrado no contexto de autenticação");
	}


}
