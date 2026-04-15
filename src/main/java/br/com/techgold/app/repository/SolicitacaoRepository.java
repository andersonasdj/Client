package br.com.techgold.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.techgold.app.dto.DashboardClienteProjection;
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

	
	@Query(value = "SELECT s.id, s.abertoPor, s.afetado, s.categoria, "
			+ "s.classificacao, s.descricao, s.formaAbertura, s.duracao, "
			+ "s.local, s.observacao, s.prioridade, s.resolucao, c.vip, c.redFlag, "
			+ "s.solicitante, s.status, c.nomeCliente, f.nomeFuncionario, s.dataAbertura, s.dataAtualizacao "
			+ "FROM solicitacoes s "
			+ "INNER JOIN clientes c ON s.cliente_id=c.id "
			+ "LEFT JOIN funcionarios f ON s.funcionario_id=f.id "
			+ "WHERE s.status = :status "
			+ "AND s.cliente_id = :id " 
			+ "AND s.excluido = :excluida ORDER BY s.id DESC LIMIT 200",nativeQuery = true)
	public List<SolicitacaoProjecao> listarSolicitacoesFinalizadas(String status, Boolean excluida, Long id);
	
	
	
	@Query(
		    value = """
		        SELECT
		            -- Local
		            COALESCE(SUM(CASE WHEN s.local = 'ONSITE' THEN 1 ELSE 0 END), 0) AS onsite,
		    		COALESCE(SUM(CASE WHEN s.local = 'OFFSITE' THEN 1 ELSE 0 END), 0) AS offsite,
		            
		            -- Forma de abertura
		            COALESCE(SUM(CASE WHEN s.formaAbertura = 'EMAIL' THEN 1 ELSE 0 END), 0) AS email,
				    COALESCE(SUM(CASE WHEN s.formaAbertura = 'TELEFONE' THEN 1 ELSE 0 END), 0) AS telefone,
				    COALESCE(SUM(CASE WHEN s.formaAbertura = 'LOCAL' THEN 1 ELSE 0 END), 0) AS local,
				    COALESCE(SUM(CASE WHEN s.formaAbertura = 'WHATSAPP' THEN 1 ELSE 0 END), 0) AS whatsapp,
				    COALESCE(SUM(CASE WHEN s.formaAbertura = 'PROATIVO' THEN 1 ELSE 0 END), 0) AS proativo,

		            -- Classificação
		            COALESCE(SUM(CASE WHEN s.classificacao = 'PROBLEMA' THEN 1 ELSE 0 END),0) AS problema,
		            COALESCE(SUM(CASE WHEN s.classificacao = 'INCIDENTE' THEN 1 ELSE 0 END),0) AS incidente,
		            COALESCE(SUM(CASE WHEN s.classificacao = 'SOLICITACAO' THEN 1 ELSE 0 END),0) AS solicitacao,
		            COALESCE(SUM(CASE WHEN s.classificacao = 'BACKUP' THEN 1 ELSE 0 END),0) AS backup,
		            COALESCE(SUM(CASE WHEN s.classificacao = 'ACESSO' THEN 1 ELSE 0 END),0) AS acesso,
		            COALESCE(SUM(CASE WHEN s.classificacao = 'EVENTO' THEN 1 ELSE 0 END),0) AS evento,

		            -- Prioridade
		            COALESCE(SUM(CASE WHEN s.prioridade = 'BAIXA' THEN 1 ELSE 0 END),0) AS baixa,
		            COALESCE(SUM(CASE WHEN s.prioridade = 'MEDIA' THEN 1 ELSE 0 END),0) AS media,
		            COALESCE(SUM(CASE WHEN s.prioridade = 'ALTA' THEN 1 ELSE 0 END),0) AS alta,
		            COALESCE(SUM(CASE WHEN s.prioridade = 'CRITICA' THEN 1 ELSE 0 END),0) AS critica,
		            COALESCE(SUM(CASE WHEN s.prioridade = 'PLANEJADA' THEN 1 ELSE 0 END),0) AS planejada,

		            -- Status
		            COALESCE(SUM(CASE WHEN s.status = 'ABERTO' THEN 1 ELSE 0 END), 0) AS aberto,
				    COALESCE(SUM(CASE WHEN s.status = 'ANDAMENTO' THEN 1 ELSE 0 END), 0) AS andamento,
				    COALESCE(SUM(CASE WHEN s.status = 'AGENDADO' THEN 1 ELSE 0 END), 0) AS agendado,
				    COALESCE(SUM(CASE WHEN s.status = 'AGUARDANDO' THEN 1 ELSE 0 END), 0) AS aguardando,
				    COALESCE(SUM(CASE WHEN s.status = 'PAUSADO' THEN 1 ELSE 0 END), 0) AS pausado,
				    COALESCE(SUM(CASE WHEN s.status = 'FINALIZADO' THEN 1 ELSE 0 END), 0) AS finalizado,

		            -- Finalizadas no mês corrente
		            COALESCE(SUM(
		                CASE
		                    WHEN s.status = 'FINALIZADO'
		                     AND s.dataFinalizado >= :inicioMes
		                    THEN 1 ELSE 0
		                END
		            ), 0) AS totalMesCorrente,

		            -- Minutos finalizados no mês corrente
		            COALESCE(
		                SUM(
		                    CASE
		                        WHEN s.status = 'FINALIZADO'
		                         AND s.dataFinalizado >= :inicioMes
		                        THEN s.duracao
		                        ELSE 0
		                    END
		                ), 0
		            ) AS totalMinutosMes

		        FROM solicitacoes s
		        WHERE s.cliente_id = :clienteId
		          AND s.excluido = false
		    """,
		    nativeQuery = true
		)
		DashboardClienteProjection gerarDashboardCliente(
		    @Param("clienteId") Long clienteId,
		    @Param("inicioMes") LocalDateTime inicioMes
		);
	
		@Query(
		    value = """
		        SELECT COUNT(s.id)
		        FROM solicitacoes s
		        WHERE s.excluido = :excluida
		          AND s.cliente_id = :clienteId
		          AND s.dataAbertura >= CURRENT_DATE
		          AND s.dataAbertura < CURRENT_DATE + INTERVAL 1 DAY
		    """,
		    nativeQuery = true
		)
		Long listarSolicitacoesAbertasHojeQtd(@Param("excluida") Boolean excluida, @Param("clienteId") Long clienteId);	
		
		@Query(
		    value = """
		        SELECT COUNT(s.id)
		        FROM solicitacoes s
		        WHERE s.excluido = :excluida
		          AND s.cliente_id = :clienteId
		          AND s.dataAtualizacao >= CURRENT_DATE
		          AND s.dataAtualizacao < CURRENT_DATE + INTERVAL 1 DAY
		    """,
		    nativeQuery = true
		)
		Long listarSolicitacoesAtualizadasHojeQtd(@Param("excluida") Boolean excluida, @Param("clienteId") Long clienteId);	
		
		@Query(
		    value = """
		        SELECT COUNT(s.id)
		        FROM solicitacoes s
		        WHERE s.excluido = :excluida
		          AND s.cliente_id = :clienteId
		          AND s.dataFinalizado >= CURRENT_DATE
		          AND s.dataFinalizado < CURRENT_DATE + INTERVAL 1 DAY
		    """,
		    nativeQuery = true
		)
		Long listarSolicitacoesFinalizadasHojeQtd(@Param("excluida") Boolean excluida, @Param("clienteId") Long clienteId);	
	
	
		@Query(value = """
		        SELECT s.id, s.abertoPor, s.afetado, s.categoria,
		               s.classificacao, s.descricao, s.formaAbertura, c.redFlag, s.duracao, s.log_id,
		               s.local, s.observacao, s.prioridade, s.resolucao, c.vip, s.versao, s.dataAgendado,
		               s.solicitante, s.status, c.nomeCliente, f.nomeFuncionario,
		               s.dataAbertura, s.dataAtualizacao, s.dataAndamento
		        FROM solicitacoes s
		        INNER JOIN clientes c ON s.cliente_id = c.id
		        LEFT JOIN funcionarios f ON s.funcionario_id = f.id
		        WHERE s.excluido = false
				  AND s.cliente_id = :clienteId
		          AND s.dataAtualizacao >= CURRENT_DATE
		          AND s.dataAtualizacao < CURRENT_DATE + INTERVAL 1 DAY
		    """,
		    nativeQuery = true
		)
		Page<SolicitacaoProjecao> listarSolicitacoesAtualizadasHoje(Pageable page, @Param("clienteId") Long clienteId);
		
		@Query(nativeQuery = true,
			    value = """
			        SELECT s.id, s.abertoPor, s.afetado, s.categoria,
			               s.classificacao, s.descricao, s.formaAbertura, c.redFlag, s.duracao,
			               s.local, s.observacao, s.prioridade, s.resolucao, c.vip, s.versao,
			               s.solicitante, s.status, c.nomeCliente, f.nomeFuncionario,
			               s.dataAbertura, s.dataAtualizacao
			        FROM solicitacoes s
			        INNER JOIN clientes c ON s.cliente_id = c.id
			        LEFT JOIN funcionarios f ON s.funcionario_id = f.id
			        WHERE s.excluido = false
			    	  AND s.cliente_id = :clienteId
			          AND s.status = 'FINALIZADO'
			          AND s.dataFinalizado >= CURRENT_DATE
			          AND s.dataFinalizado < CURRENT_DATE + INTERVAL 1 DAY
			    """)
			Page<SolicitacaoProjecao> listarSolicitacoesFinalizadasHoje(Pageable page, @Param("clienteId") Long clienteId);
		
		
		@Query(nativeQuery = true,
			    value = """
			        SELECT s.id, s.abertoPor, s.afetado, s.categoria,
			               s.classificacao, s.descricao, s.formaAbertura, c.redFlag, s.duracao,
			               s.local, s.observacao, s.prioridade, s.resolucao, c.vip, s.versao, s.dataAgendado,
			               s.solicitante, s.status, c.nomeCliente, f.nomeFuncionario,
			               s.dataAbertura, s.dataAtualizacao, s.dataAndamento
			        FROM solicitacoes s
			        INNER JOIN clientes c ON s.cliente_id = c.id
			        LEFT JOIN funcionarios f ON s.funcionario_id = f.id
			        WHERE s.excluido = false
			          AND s.cliente_id = :clienteId
			          AND s.status = 'ABERTO'
			          AND s.dataAbertura >= CURRENT_DATE
			          AND s.dataAbertura < CURRENT_DATE + INTERVAL 1 DAY
			    """)
			Page<SolicitacaoProjecao> listarSolicitacoesAbertasHoje(Pageable page, @Param("clienteId") Long clienteId);
}
