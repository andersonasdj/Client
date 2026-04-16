package br.com.techgold.app.services.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.techgold.app.model.CustomUserDetails;
import br.com.techgold.app.repository.ClienteRepository;
import br.com.techgold.app.repository.ColaboradorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ColaboradorRepository colaboradorRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {

        var cliente = clienteRepo.findByUsername(username);

        if (cliente.isPresent()) {
            return cliente.get(); // já implementa UserDetails
        }

        var colab = colaboradorRepo.findByUsername(username);

        if (colab.isPresent()) {
            return new CustomUserDetails(
                colab.get().getUsername(),
                colab.get().getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR")),
                colab.get()
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}