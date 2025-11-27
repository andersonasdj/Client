package br.com.techgold.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cliente")
public class ClienteController {
	
	
	@GetMapping("/perfil")
	public String perfil() {
		return "perfil.html";
		
	}

}
