package br.com.techgold.app.restcontroller;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DtoClienteHome;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.Colaborador;
import br.com.techgold.app.model.CustomUserDetails;
import br.com.techgold.app.services.ClienteService;

@RestController
@RequestMapping("clientes")
public class ClienteRestController {
	
	@Autowired ClienteService service;
	
	
	@GetMapping("/home")
	public ResponseEntity<DtoClienteHome> funcionarioHome() {

	    var auth = SecurityContextHolder.getContext().getAuthentication();
	    Object principal = auth.getPrincipal();

	    Cliente cliente = null;
	    String nomeExibicao = null;

	    // LOGIN DIRETO (CLIENTE)
	    if (principal instanceof Cliente c) {

	        cliente = service.buscaPorNome(c.getNomeCliente());
	        nomeExibicao = cliente.getNomeCliente();

	        return ResponseEntity.ok().body(
	            new DtoClienteHome(
	                nomeExibicao,
	                cliente.getDataUltimoLogin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
	                service.buscaSolicitacoes(cliente), // TODAS
	                cliente.getId(),
	                service.buscaUltimasSolicitacoes(cliente.getId())
	            )
	        );
	    }

	    // LOGIN VIA CUSTOM
	    else if (principal instanceof CustomUserDetails custom) {

	        Object entidade = custom.getEntidade();

	        // CLIENTE via custom
	        if (entidade instanceof Cliente c) {

	            cliente = service.buscaPorNome(c.getNomeCliente());
	            nomeExibicao = cliente.getNomeCliente();

	            return ResponseEntity.ok().body(
	                new DtoClienteHome(
	                    nomeExibicao,
	                    cliente.getDataUltimoLogin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
	                    service.buscaSolicitacoes(cliente),
	                    cliente.getId(),
	                    service.buscaUltimasSolicitacoes(cliente.getId())
	                )
	            );
	        }

	        // COLABORADOR
	        else if (entidade instanceof Colaborador colab) {
	            cliente = colab.getCliente();
	            nomeExibicao = colab.getNomeColaborador(); // AQUI MUDA

	            return ResponseEntity.ok().body(
	                new DtoClienteHome(
	                    nomeExibicao,
	                    cliente.getDataUltimoLogin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
	                    //  SOMENTE DELE
	                    service.buscaSolicitacoes(cliente),
	                    cliente.getId(),
	                    //  SOMENTE DELE
	                    service.buscaUltimasSolicitacoes(cliente.getId())
	                )
	            );
	        }
	    }

	    throw new RuntimeException("Usuário não identificado no contexto de autenticação");
	}

	
//	@GetMapping("/home") //RETORNA UMA DTO COM OS DADOS PARA A HOME PAGE
//	public ResponseEntity<DtoClienteHome> funcionarioHome() {
//	
//		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
//		
//		return ResponseEntity.ok().body(
//				new DtoClienteHome(
//						cliente.getNomeCliente(), 
//						cliente.getDataUltimoLogin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
//						service.buscaSolicitacoes(cliente),
//						cliente.getId(),
//						service.buscaUltimasSolicitacoes(cliente.getId())
//				));
//	}
	
	
	
}
