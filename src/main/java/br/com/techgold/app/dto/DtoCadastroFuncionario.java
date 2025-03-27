package br.com.techgold.app.dto;

import br.com.techgold.app.model.UserRole;
import jakarta.validation.constraints.NotBlank;

public record DtoCadastroFuncionario(
		
		@NotBlank
		String nomeFuncionario,
		@NotBlank
		String username,
		@NotBlank
		String password,
		UserRole role) {

}
