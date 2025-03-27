package br.com.techgold.app.security;

public record RegisterDTO(
		String username,
		String password,
		String role
		) {

}
