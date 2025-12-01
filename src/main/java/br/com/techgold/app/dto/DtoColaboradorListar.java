package br.com.techgold.app.dto;

import br.com.techgold.app.model.Colaborador;

public record DtoColaboradorListar(
		Long id,
		String nomeColaborador,
		String celular,
		boolean vip,
		Long clienteId,
		String email
		
		) {
	
	public DtoColaboradorListar(Colaborador c) {
		this(c.getId(), c.getNomeColaborador(), c.getCelular(), c.isVip(), c.getId(), c.getEmail());
	}

}
