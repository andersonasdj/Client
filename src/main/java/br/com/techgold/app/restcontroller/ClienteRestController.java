package br.com.techgold.app.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DadosClienteEditDTO;
import br.com.techgold.app.dto.DtoAtualizarCliente;
import br.com.techgold.app.dto.DtoCadastroCliente;
import br.com.techgold.app.dto.DtoClienteList;
import br.com.techgold.app.repository.ClienteRepository;
import br.com.techgold.app.services.ClienteService;

@RestController
@RequestMapping("clientes")
public class ClienteRestController {
	
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private ClienteService service;
	
	@GetMapping("nome/{conteudo}") //RETORNA DTO COM PROJEÇÃO DOS DADOS NECESSÀRIO COM NATIVE QUERY
	public Page<DtoClienteList> buscarClientePorPalavras(@PathVariable String conteudo,  @PageableDefault(size = 100, sort= {"id"}, direction = Direction.DESC) Pageable page) {
		return service.listarClientePorPalavra(page,conteudo);
	}
	
	@GetMapping
	public ResponseEntity<Page<DtoClienteList>> listar(@PageableDefault(size = 15, sort= {"nomeCliente"}, direction = Direction.ASC) Pageable page){
		var todosOsClientes = service.listarTodos(page);
		return ResponseEntity.ok(todosOsClientes);
	}
	
	@GetMapping("/filtro/{bairro}")
	public List<DtoClienteList> listarFiltro(@PathVariable String bairro){
		return repository.listarClientesPorBairro(bairro).stream().map(DtoClienteList::new).toList();
	}
	
	@GetMapping("/filtro/vip/{vip}")
	public List<DtoClienteList> listarFiltroVip(@PathVariable String vip){
		if(vip.equals("vip")) {
			return repository.listarClientesVip().stream().map(DtoClienteList::new).toList();
		}else if(vip.equals("redflag")) {
			return repository.listarClientesRedFlag().stream().map(DtoClienteList::new).toList();
		}else {
			return repository.listarClientesRedFlagEVip().stream().map(DtoClienteList::new).toList();
		}
	} 
	
	@GetMapping("/nomes")
	public List<String> listaClientesNome(){
		return repository.listarNomesCliente();
	}
	
	@GetMapping("/edit/{id}")
	public DadosClienteEditDTO editar(@PathVariable Long id ) {
		if(repository.existsById(id)) {
			return new DadosClienteEditDTO(repository.getReferenceById(id));			
		}else {
			return null;
		}
	}
	
	@PostMapping
	public void cadastrar(@RequestBody DtoCadastroCliente dados) {
		service.cadastrarNovoCliente(dados);
	}
	
	@PutMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public DtoAtualizarCliente atualizar(@RequestBody DtoAtualizarCliente dados) {
		return new DtoAtualizarCliente(service.atualizarCliente(dados));
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/delete/{id}")
	public void deletar(@PathVariable Long id ) {
		repository.deleteById(id);
	}
	
	@GetMapping("/bairros")
	public List<String> listaBairrosClientes(){
		return service.listarBairrosClientes();
	}
	
}
