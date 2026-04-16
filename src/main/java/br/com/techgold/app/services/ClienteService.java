package br.com.techgold.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import br.com.techgold.app.dto.DtoSolicitacoesCliente;
import br.com.techgold.app.dto.DtoUltimasSolicitacoes;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {
	
	@Autowired ClienteRepository repository;
	
	public Cliente buscaPorNome(String nome) {
		return repository.findBynomeCliente(nome);
	}
	
//	public Optional<Cliente> buscaPorUserDetails(String nome) {
	public Optional<Cliente> buscaPorUserDetails(String nome) {
		return repository.findByUsername(nome);
	}

	public Cliente buscaClientePorNome(Long dados) {
		return  repository.getReferenceById(dados);
	}
	
	@Transactional
	public void atualizaImagem(Long id, String caminhoFoto) {
		Cliente cliente = repository.getReferenceById(id);
		cliente.setCaminhoFoto(caminhoFoto);
	}
	
	public DtoSolicitacoesCliente buscaSolicitacoes(Cliente cliente) {
		
		int abertas, agendadas, andamento, aguardando, pausado, total;
		abertas = repository.buscaPorFuncionario(cliente.getId(), "ABERTO");
		andamento = repository.buscaPorFuncionario(cliente.getId(), "ANDAMENTO");
		agendadas = repository.buscaPorFuncionario(cliente.getId(), "AGENDADO");
		aguardando = repository.buscaPorFuncionario(cliente.getId(), "AGUARDANDO");
		pausado = repository.buscaPorFuncionario(cliente.getId(), "PAUSADO");
		total = abertas + agendadas + andamento + aguardando + pausado;
		return new DtoSolicitacoesCliente(abertas, andamento, agendadas, aguardando, pausado, total);
	}
	
	@Transactional
	public void atualizaIpLogin(Cliente f, String ip, String pais) {
		Cliente cliente = repository.getReferenceById(f.getId());
		cliente.setDataUltimoLogin(LocalDateTime.now().withNano(0));
		cliente.setIp(ip);
		cliente.setPais(pais);
	}

	public List<DtoUltimasSolicitacoes> buscaUltimasSolicitacoes(Long id) {
		
		return repository.buscarUltimas(id, PageRequest.of(0, 5));
	}
	
	
	
}
