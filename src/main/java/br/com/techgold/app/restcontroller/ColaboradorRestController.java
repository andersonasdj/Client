package br.com.techgold.app.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DtoColaboradorListar;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.orm.ColaboradorProjecao;
import br.com.techgold.app.services.ClienteService;
import br.com.techgold.app.services.ColaboradorService;

@RestController
@RequestMapping("colaborador")
public class ColaboradorRestController {

	@Autowired
	private ColaboradorService service;
	@Autowired ClienteService clienteService;
	
	@GetMapping("/list") //RETORNA UMA PROJECAO DE COLABORADORES POR ID DE CLIENTE
	public ResponseEntity<List<ColaboradorProjecao>> listarPorIdCliente( ) {
		Cliente cliente = clienteService.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		System.out.println("AQUI");
		return ResponseEntity.ok().body(service.listarPorIdCliente(cliente.getId()));
	}
	
	@GetMapping("/list/cliente/{id}") //RETORNA UMA PROJECAO DE COLABORADORES POR ID DE CLIENTE
	public ResponseEntity<List<String>> listarNomesPorIdCliente(@PathVariable Long id ) {
		return ResponseEntity.ok().body(service.listarNomesIdCliente(id));
	}
	
	@GetMapping("/list/cliente/{id}/{nomeColaborador}") //RETORNA UMA PROJECAO DE COLABORADORES POR ID DE CLIENTE
	public ResponseEntity<String> listarNomesCelularPorIdCliente(@PathVariable Long id, @PathVariable String nomeColaborador ) {
		return ResponseEntity.ok().body(service.listarCelularColaborador(id, nomeColaborador));
	}
	
	
	@GetMapping //REVISAR !!!!!
	public ResponseEntity<List<DtoColaboradorListar>> listar() {
		return ResponseEntity.ok().body(service.listar());
	}
	
	
	
	

	
}
