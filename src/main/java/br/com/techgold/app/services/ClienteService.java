package br.com.techgold.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired ClienteRepository repository;
	
	public Cliente buscaPorNome(String nome) {
		return repository.findBynomeCliente(nome);
	}
	
	public UserDetails buscaPorUserDetails(String nome) {
		return repository.findByUsername(nome);
	}

	public Cliente buscaClientePorNome(Long dados) {
		return  repository.getReferenceById(dados);
	}
	
}
