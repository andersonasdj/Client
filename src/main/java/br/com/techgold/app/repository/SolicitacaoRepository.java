package br.com.techgold.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.techgold.app.model.Solicitacao;
import br.com.techgold.app.orm.SolicitacaoProjecao;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long>{
	
	
	@Query(value = "SELECT s.id, s.abertoPor, s.afetado, s.categoria, c.nomeCliente, s.duracao, "
			+ "s.classificacao, s.descricao, s.formaAbertura, c.redFlag, s.status, s.peso, "
			+ "s.local, s.observacao, s.prioridade, s.resolucao, c.vip, s.solicitante, s.versao, "
			+ "f.nomeFuncionario, s.dataAbertura, s.dataAtualizacao, s.dataAgendado, s.log_id, s.dataAndamento "
			+ "FROM solicitacoes s "
			+ "INNER JOIN clientes c ON s.cliente_id=c.id "
			+ "LEFT JOIN funcionarios f ON s.funcionario_id=f.id "
			+ "WHERE s.cliente_id=:idCliente "
			+ "AND s.status != :status " 
			+ "AND s.excluido = :excluida",nativeQuery = true)
	public Page<SolicitacaoProjecao> listarSolicitacoes(Pageable page, String status, Boolean excluida, Long idCliente);

}
