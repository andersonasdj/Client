package br.com.techgold.app.restcontroller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.techgold.app.dto.DtoClienteHome;
import br.com.techgold.app.model.Cliente;
import br.com.techgold.app.services.ClienteService;

@RestController
@RequestMapping("clientes")
public class ClienteRestController {
	
	@Autowired ClienteService service;

	
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/home") //RETORNA UMA DTO COM OS DADOS PARA A HOME PAGE
	public ResponseEntity<DtoClienteHome> funcionarioHome() {
	
		Cliente cliente = service.buscaPorNome(((Cliente) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getNomeCliente());
		Date dataHoje = new Date();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		DateFormat dateFormatNumber = new SimpleDateFormat("HH");
		
		System.out.println("CLIENTE : " + cliente.getNomeCliente() + cliente.getDataUltimoLogin() + cliente.getId());
		
		int hora = Integer.valueOf( dateFormatNumber.format(dataHoje)); 
		String saudacao;
		
		if(hora >= 0 && hora < 12) {
			saudacao = "Bom dia, ";
		} else if (hora >= 12 && hora < 18) {
			saudacao = "Boa tarde, ";
		} else {
			saudacao = "Boa noite, ";
		}
		
		return ResponseEntity.ok().body(
				new DtoClienteHome(
						saudacao, 
						cliente.getNomeCliente(), 
						dateFormat.format(dataHoje).toString(),
						cliente.getDataUltimoLogin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
						service.buscaSolicitacoes(cliente),
						cliente.getId()
				));
	}
	
	
	
}
