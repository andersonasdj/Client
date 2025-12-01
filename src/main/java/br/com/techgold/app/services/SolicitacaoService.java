package br.com.techgold.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.techgold.app.model.Solicitacao;
import br.com.techgold.app.orm.SolicitacaoProjecao;
import br.com.techgold.app.repository.SolicitacaoRepository;

@Service
public class SolicitacaoService {
	
	@Autowired private SolicitacaoRepository repository;
	
	public Page<SolicitacaoProjecao> listarSolicitacoes(String status, Boolean exluida, Long idCliente, Pageable page) {
		return repository.listarSolicitacoes(page, status, exluida, idCliente);
	}
	
	public Solicitacao buscarPorId(Long id) {
		return repository.getReferenceById(id);
	}

}
