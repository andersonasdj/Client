package br.com.techgold.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("solicitacao")
public class SolicitacaoController {

	@GetMapping("/form")
	public String formulario() {
		
		return "solicitacaoForm.html";
	}
		
	@GetMapping("/list")
	public String listar() {
		
		return "solicitacaoList.html";
	}
	
	@GetMapping("/relatorios")
	public String relatorios() {
		
		return "solicitacaoOpcoesRelatorios.html";
	}
	
	@GetMapping("/finalizadas")
	public String finalizadas() {
		
		return "solicitacaoFinalizadas.html";
	}
	
	@GetMapping("/dashboard/geral")
	public String dashboardAtivos() {
		
		return "dashboard.html";
	}
	
	@GetMapping("/dashboard/cliente")
	public String dashboardCliente() {
		
		return "dashboardCliente.html";
	}
	
}
