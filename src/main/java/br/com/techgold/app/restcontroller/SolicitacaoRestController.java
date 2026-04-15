package br.com.techgold.app.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DtoCadastroSolicitacao;
import br.com.techgold.app.dto.DtoDadosEdicaoRapidaMaisFuncionarios;
import br.com.techgold.app.dto.DtoDadosParaSolicitacao;
import br.com.techgold.app.dto.DtoDashboardCliente;
import br.com.techgold.app.dto.DtoSolicitacaoRelatorios;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.Funcionario;
import br.com.techgold.app.model.Solicitacao;
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
	
	@GetMapping("/excluido") //RETORNA DTO COM PROJEÇÃO DAS SOLICITAÇÕES EXCLUIDAS-LIXEIRA
	public Page<SolicitacaoProjecao> excluidas(@PageableDefault(size = 200, sort= {"id"}, direction = Direction.DESC) Pageable page) {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		return solicitacaoService.listarSolicitacoes(page,Status.FINALIZADO.toString(), true, cliente.getId());
	}
	
	@GetMapping("/finalizado") //RETORNA DTO COM PROJEÇÃO DE TODAS AS SOLICITACOES EXCLUÍDAS-LIXEIRA
	public List<SolicitacaoProjecao> finalizados() {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		return solicitacaoService.listarSolicitacoesFinalizadas(Status.FINALIZADO.toString(), false, cliente.getId());
	}
	
	@GetMapping("/getData") //RETORNA LISTAGEM DE CLIENTES E FUNCIONARIOS ATIVOS PARA LISTAGEM DO SELECTBOX ### CACHE ###
	private DtoDadosParaSolicitacao coletaDadosParaSolicitacao() {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		return new DtoDadosParaSolicitacao(cliente.getNomeCliente(), cliente.getId().toString());
	}
	
	@PostMapping //SALVA UMA NOVA SOLICITAÇÃO NO BANCO
	public String cadastrarNova(@RequestBody DtoCadastroSolicitacao dados ) {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		Solicitacao solicitacao = new Solicitacao(dados, cliente);
		Funcionario funcionario = repositoryFuncionario.buscarPorNome("Suporte");
		solicitacao.setFuncionario(funcionario);
		return solicitacaoService.salvarNovaSolicitacao(solicitacao);
	}
	
	@GetMapping("/dashboard/cliente") //RETORNA UMA DTO COM TODOS OS DADOS PARA O DASHBOARD POR CLIENTE
	public DtoDashboardCliente dashboardCliente() {
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		return solicitacaoService.geraDashboardCliente(cliente.getId());
	}
	
	@GetMapping("/relatorio") //RETORNA UMA DTO COM TODOS OS DADOS PARA A VIEWER DE RELATORIOS
	public DtoSolicitacaoRelatorios relatorios() {
		return solicitacaoService.geraRelatorios();
	}
	
	@GetMapping("/relatorio/{status}/hoje")
	public Page<SolicitacaoProjecao> listarRelatorioAtualizadasHoje(@PathVariable String status, @PageableDefault(size = 50, sort= {"id"}, direction = Direction.DESC) Pageable page) {
		return solicitacaoService.listarSolicitacoesRelatorioHoje(page, status);
	}

}
