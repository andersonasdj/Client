package br.com.techgold.app.dto;

import br.com.techgold.app.model.Solicitacao;
import br.com.techgold.app.model.enums.Classificacao;
import br.com.techgold.app.model.enums.Prioridade;
import br.com.techgold.app.model.enums.Status;

public record DtoUltimasSolicitacoes(
		Long id,
		String descricao,
		Status status,
		Prioridade prioridade,
		Classificacao classificacao) {

	
	public DtoUltimasSolicitacoes(Solicitacao dados) {
		this(
				dados.getId(),
				dados.getDescricao(),
				dados.getStatus(),
				dados.getPrioridade(),
				dados.getClassificacao()
				);
	}
}
