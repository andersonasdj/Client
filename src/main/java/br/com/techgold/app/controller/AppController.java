package br.com.techgold.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
	
	
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
	

}
