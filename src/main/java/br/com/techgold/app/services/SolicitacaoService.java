package br.com.techgold.app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.techgold.app.dto.DashboardClienteProjection;
import br.com.techgold.app.dto.DtoDashboardCliente;
import br.com.techgold.app.dto.DtoSolicitacaoRelatorios;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.model.Colaborador;
import br.com.techgold.app.model.CustomUserDetails;
import br.com.techgold.app.model.Solicitacao;
import br.com.techgold.app.orm.SolicitacaoProjecao;
import br.com.techgold.app.repository.SolicitacaoRepository;

@Service
public class SolicitacaoService {
	
	@Autowired private SolicitacaoRepository repository;
	@Autowired ClienteService service;
	
	public Page<SolicitacaoProjecao> listarSolicitacoes(String status, Boolean exluida, Long idCliente, Pageable page) {
		return repository.listarSolicitacoes(page, status, exluida, idCliente);
	}
	
	public Solicitacao buscarPorId(Long id) {
		return repository.getReferenceById(id);
	}
	
	public Page<SolicitacaoProjecao> listarSolicitacoes(Pageable page, String status, Boolean exluida, Long id) {
		return repository.listarSolicitacoes(page, status, exluida, id);
	}
	
	public List<SolicitacaoProjecao> listarSolicitacoesFinalizadas(String status, Boolean exluida, Long id) {
		return repository.listarSolicitacoesFinalizadas(status, exluida, id);
	}

	public String salvarNovaSolicitacao(Solicitacao solicitacao) {
		Cliente cliente = getClienteLogado();
		solicitacao.setAbertoPor(cliente.getNomeCliente());
		solicitacao.setPeso(0l);
		solicitacao.setVersao(0);
		repository.save(solicitacao);
		return "Solicitação cadastrada";
	}
	
	public DtoDashboardCliente geraDashboardCliente(Long id) {

	    LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
	    DashboardClienteProjection p = repository.gerarDashboardCliente(id, inicioMes);
	    int totalSolicitacoes = p.getAberto() + p.getAndamento() + p.getAgendado() + p.getAguardando() + p.getPausado() + p.getFinalizado();

	    return new DtoDashboardCliente(
	        p.getOnsite(),
	        p.getOffsite(),
	        p.getProblema(),
	        p.getIncidente(),
	        p.getSolicitacao(),
	        p.getBackup(),
	        p.getAcesso(),
	        p.getEvento(),
	        p.getBaixa(),
	        p.getMedia(),
	        p.getAlta(),
	        p.getCritica(),
	        p.getPlanejada(),
	        p.getAberto(),
	        p.getAndamento(),
	        p.getAgendado(),
	        p.getAguardando(),
	        p.getPausado(),
	        p.getFinalizado(),
	        totalSolicitacoes,
	        p.getTotalMesCorrente(),
	        p.getTotalMinutosMes(),
	        p.getEmail(),
	        p.getTelefone(),
	        p.getLocal(),
	        p.getWhatsapp(),
	        p.getProativo()
	    );
	}
	
		public DtoSolicitacaoRelatorios geraRelatorios() {
		
			Cliente cliente = getClienteLogado();
			Long abertos, finalizados, atualizadas;
			abertos = repository.listarSolicitacoesAbertasHojeQtd(false, cliente.getId());
			finalizados = repository.listarSolicitacoesFinalizadasHojeQtd(false, cliente.getId());
			atualizadas = repository.listarSolicitacoesAtualizadasHojeQtd(false, cliente.getId());
			
			return new DtoSolicitacaoRelatorios(abertos,finalizados,atualizadas);
		}
		
		public Page<SolicitacaoProjecao> listarSolicitacoesRelatorioHoje(Pageable page, String status) {
			Cliente cliente = getClienteLogado();
			if(status.equals("atualizadas")) {
				return repository.listarSolicitacoesAtualizadasHoje(page, cliente.getId());
			} else if(status.equals("finalizadas")) {
				return repository.listarSolicitacoesFinalizadasHoje(page, cliente.getId());
			}else if(status.equals("abertas")) {
				return repository.listarSolicitacoesAbertasHoje(page, cliente.getId());
			}else {
				return null;
			}
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
