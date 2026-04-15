package br.com.techgold.app.dto;

import br.com.techgold.app.model.enums.Categoria;
import br.com.techgold.app.model.enums.Classificacao;
import br.com.techgold.app.model.enums.FormaAbertura;
import br.com.techgold.app.model.enums.Local;
import br.com.techgold.app.model.enums.Prioridade;
import br.com.techgold.app.model.enums.Status;

public record DtoCadastroSolicitacao(
		String solicitante,
		String afetado,
		String descricao,
		String observacao,
		Prioridade prioridade,
		FormaAbertura formaAbertura,
		Categoria categoria,
		Classificacao classificacao,
		Local local,
		Status status
		
		) {
		

}
