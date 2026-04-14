package br.com.techgold.app.dto;

public record DtoClienteHome(
		String saudacao,
		String nomeFuncionario,
		String dataHoje,
		String dataUltimoLogin,
		DtoSolicitacoesCliente solicitacoes,
		Long id
		) {
}
