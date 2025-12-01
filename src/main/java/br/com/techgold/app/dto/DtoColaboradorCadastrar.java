package br.com.techgold.app.dto;

public record DtoColaboradorCadastrar(
		String nomeColaborador,
		String celular,
		boolean vip,
		Long clienteId,
		String email
		) {

}
