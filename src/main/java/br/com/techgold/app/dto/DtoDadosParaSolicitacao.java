package br.com.techgold.app.dto;

public record DtoDadosParaSolicitacao(
		
		String clientes,
		String clientesId
		) {
		public DtoDadosParaSolicitacao(String clientes, String clientesId) {
			this.clientes = clientes;
			this.clientesId = clientesId;
		}

}
