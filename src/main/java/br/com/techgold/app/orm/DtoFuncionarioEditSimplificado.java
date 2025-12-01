package br.com.techgold.app.orm;

import java.time.LocalDateTime;

public interface DtoFuncionarioEditSimplificado {

	Long getId(); 
	String getNomeFuncionario(); 
	String getUsername(); 
	LocalDateTime getDataAtualizacao();

}
