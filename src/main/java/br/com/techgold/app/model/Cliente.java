package br.com.techgold.app.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.techgold.app.dto.DtoAtualizarCliente;
import br.com.techgold.app.dto.DtoCadastroCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Usuario implements UserDetails{
	
	
	private static final long serialVersionUID = 1L;
	@Column(length = 100)
	private String nomeCliente;
	@Column(length = 200)
	private String endereco;
	@Column(length = 20)
	private String telefone;
	@Column(length = 20)
	private String cnpj;
	private boolean redFlag;
	private boolean vip;
	@Column(length = 20)
	private String bairro;
	
	@Enumerated(EnumType.STRING)
	private UserRole role;
	
	public Cliente(DtoCadastroCliente dados) {
		
		this.nomeCliente = dados.nomeCliente();
		this.setUsername(dados.username());
		this.setPassword(new BCryptPasswordEncoder().encode(dados.password().toString()));
		this.setAtivo(true);
		this.setMfa(false);
		this.setDataAtualizacao(LocalDateTime.now().withNano(0));
		this.endereco = dados.endereco();
		this.telefone = dados.telefone();
		this.cnpj = dados.cnpj();
		this.redFlag = false;
		this.vip = false;
		this.bairro = dados.bairro();
		this.setRole(UserRole.USER);
	}

	public Cliente(DtoAtualizarCliente dados) {
		
		this.setId(dados.id());
		this.nomeCliente = dados.nomeCliente();
		this.setUsername(dados.username());
		this.setPassword(new BCryptPasswordEncoder().encode(dados.password().toString()));
		this.setAtivo(dados.ativo());
		this.setMfa(false);
		this.setDataAtualizacao(LocalDateTime.now().withNano(0));
		this.endereco = dados.endereco();
		this.telefone = dados.telefone();
		this.cnpj = dados.cnpj();
		this.redFlag = dados.redFlag();
		this.vip = dados.vip();
		this.bairro = dados.bairro();
		this.setRole(UserRole.USER);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(this.role == UserRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_EDITOR") );
		else if(this.role == UserRole.EDITOR) return List.of(new SimpleGrantedAuthority("ROLE_EDITOR"),new SimpleGrantedAuthority("ROLE_USER"));
		else{return List.of(new SimpleGrantedAuthority("ROLE_USER"));}
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
