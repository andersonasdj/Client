package br.com.techgold.app.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.enums.Status;
import br.com.techgold.app.orm.SolicitacaoProjecao;
import br.com.techgold.app.services.ClienteService;
import br.com.techgold.app.services.SolicitacaoService;

@RestController
@RequestMapping("api/v1/solicitacao")
public class SolicitacaoRestController {
	
	@Autowired SolicitacaoService solicitacaoService;
	
	@Autowired ClienteService service;
	
	@GetMapping("short") //RETORNA DTO COM PROJEÇÃO DOS DADOS NECESSÀRIO COM NATIVE QUERY
	public Page<SolicitacaoProjecao> listaResumidaNaoFinalizados(@PageableDefault(size = 100, sort= {"peso"}, direction = Direction.DESC) Pageable page) {
		System.out.println("EM REST CONTROLLER");
		
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		System.out.println("CLIENTE: "+ cliente.getNomeCliente());
		
		return solicitacaoService.listarSolicitacoes(page,Status.FINALIZADO.toString(), false, cliente.getId());
	}
	

}
