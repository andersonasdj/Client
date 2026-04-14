package br.com.techgold.app.dto;

public record DtoSolicitacoesCliente(
		int abertas,
		int andamento,
		int agendados,
		int aguardando,
		int pausado,
		int total) {

}
