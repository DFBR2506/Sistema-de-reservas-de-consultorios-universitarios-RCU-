package co.edu.unimagdalena.RCU.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import co.edu.unimagdalena.RCU.security.domine.repositories.AppUserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class JpaUserDetailsService implements UserDetailsService{
    private AppUserRepository userRepository;

    @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByDocumentNumberIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with document number: " + username));

        var authorities = user.getRoles().stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return User.withUsername(user.getDocumentNumber())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
    
}
