package br.com.techgold.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
	
	@GetMapping("/upload")
	public String teste() {
		return "templates/upload.html";
	}
	
	@GetMapping("/impressao")
	public String impressaoSolicitacao() {
		return "templates/impressao.html";
	}
	
	@GetMapping("/impressao-relatorio")
	public String impressaoRelatorioCliente() {
		return "templates/impressao-relatorio-cliente.html";
	}
	
	@GetMapping("/login")
	public String login() {
		return "templates/login.html";
	}
	
	@GetMapping("/mfa")
	public String mfa() {
		return "templates/mfa.html";
	}
	
	@GetMapping("/home")
	public String home() {
		return "templates/home.html";
	}
	
	@GetMapping("/sobre")
	public String sobre() {
		return "templates/sobre.html";
	}
	
	@GetMapping("/avisos")
	public String avisos() {
		return "templates/avisos.html";
	}
	
	@GetMapping("/funcionalidades")
	public String funcionalidades() {
		return "templates/funcionalidade.html";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/logAcesso")
	public String logsAcesso() {
		return "templates/logsAcesso.html";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/logRefeicao")
	public String logsRefeicao() {
		return "templates/logsRefeicao.html";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/gerencia")
	public String gerencia() {
		return "templates/gerencia.html";
	}
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/configuracoes")
	public String configuracoes() {
		return "templates/configuracoes.html";
	}

}
