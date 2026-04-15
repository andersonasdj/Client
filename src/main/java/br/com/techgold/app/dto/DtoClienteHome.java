package br.com.techgold.app.dto;

import java.util.List;

public record DtoClienteHome(
		String nomeFuncionario,
		String dataUltimoLogin,
		DtoSolicitacoesCliente solicitacoes,
		Long id,
		List<DtoUltimasSolicitacoes> ultimas
		) {
}
