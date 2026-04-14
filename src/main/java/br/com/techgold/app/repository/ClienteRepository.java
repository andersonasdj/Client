package br.com.techgold.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.techgold.app.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	
	public UserDetails findByUsername(String username);
	
	@Query(value = "SELECT c.nomeCliente FROM clientes c ORDER BY c.nomeCliente", nativeQuery = true)
	public List<String> listarNomesCliente();
	
	
	public Cliente findBynomeCliente(String nomeCliente);

	
	@Query(value = "SELECT COUNT(*) FROM solicitacoes s WHERE s.cliente_id=:clienteId AND s.status=:status AND s.excluido=0", nativeQuery = true)
	public int buscaPorFuncionario(Long clienteId, String status);


}
