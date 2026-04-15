package br.com.techgold.app.dto;

import br.com.techgold.app.model.Solicitacao;
import br.com.techgold.app.model.enums.Status;

public record DtoUltimasSolicitacoes(
		Long id,
		String descricao,
		Status status) {

	
	public DtoUltimasSolicitacoes(Solicitacao dados) {
		this(
				dados.getId(),
				dados.getDescricao(),
				dados.getStatus()
				);
	}
}
