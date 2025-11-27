package br.com.techgold.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.techgold.app.orm.SolicitacaoProjecao;
import br.com.techgold.app.repository.SolicitacaoRepository;

@Service
public class SolicitacaoService {
	
	@Autowired private SolicitacaoRepository repository;
	
	public Page<SolicitacaoProjecao> listarSolicitacoes(Pageable page, String status, Boolean exluida, Long idCliente) {
		System.out.println("EM SOLICITACAO SERVICE BUSCANDO SOLICITACOES");
		return repository.listarSolicitacoes(page, status, exluida, idCliente);
	}

}
