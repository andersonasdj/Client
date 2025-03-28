package br.com.techgold.app.dto;

import java.io.Serializable;

import br.com.techgold.app.model.Cliente;

public record DtoClienteList(
		Long id,
		boolean ativo,
		String nomeCliente,
		boolean vip,
		boolean redFlag
		
		) implements Serializable {
	
	public DtoClienteList(Cliente c){
		this(c.getId(), c.getAtivo(), c.getNomeCliente(), c.isVip(), c.isRedFlag());
		
		
	}

}
