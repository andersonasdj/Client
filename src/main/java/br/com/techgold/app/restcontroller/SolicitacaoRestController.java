package br.com.techgold.app.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DtoDadosEdicaoRapidaMaisFuncionarios;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.enums.Status;
import br.com.techgold.app.orm.SolicitacaoProjecao;
import br.com.techgold.app.repository.FuncionarioRepository;
import br.com.techgold.app.services.ClienteService;
import br.com.techgold.app.services.ColaboradorService;
import br.com.techgold.app.services.SolicitacaoService;

@RestController
@RequestMapping("api/v1/solicitacao")
public class SolicitacaoRestController {
	
	@Autowired FuncionarioRepository repositoryFuncionario;
	@Autowired SolicitacaoService solicitacaoService;
	@Autowired ColaboradorService colaboradorService;
	@Autowired ClienteService service;
	
	@GetMapping("short") //RETORNA DTO COM PROJEÇÃO DOS DADOS NECESSÀRIO COM NATIVE QUERY
	public Page<SolicitacaoProjecao> listaResumidaNaoFinalizados(@PageableDefault(size = 100, sort= {"peso"}, direction = Direction.DESC) Pageable page) {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		return solicitacaoService.listarSolicitacoes(Status.FINALIZADO.toString(), false, cliente.getId(),page);
	}
	
	@GetMapping("/busca/{id}") //RETORNA UMA DTO DE UMA SOLICITAÇÃO PARA EDIÇÃO RÁPIDA
	public ResponseEntity<DtoDadosEdicaoRapidaMaisFuncionarios> buscaPorId(@PathVariable Long id) {
		return ResponseEntity.ok().body( new DtoDadosEdicaoRapidaMaisFuncionarios(solicitacaoService.buscarPorId(id)));
	}
	

}
