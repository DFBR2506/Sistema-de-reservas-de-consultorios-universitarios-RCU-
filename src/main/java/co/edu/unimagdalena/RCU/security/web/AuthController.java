package co.edu.unimagdalena.RCU.security.web;

import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unimagdalena.RCU.security.domine.entities.AppUser;
import co.edu.unimagdalena.RCU.security.domine.entities.Role;
import co.edu.unimagdalena.RCU.security.domine.repositories.AppUserRepository;
import co.edu.unimagdalena.RCU.security.dto.AuthDtos.*;
import co.edu.unimagdalena.RCU.security.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (appUserRepository.existsByDocumentNumberIgnoreCase(request.documentNumber())) {
            return ResponseEntity.badRequest().build();
        }

        var roles = Optional.ofNullable(request.roles()).filter(r -> !r.isEmpty())
            .orElseGet(() -> Set.of(Role.ROLE_USER, Role.ROLE_RECEPTIONIST));

        var user = AppUser.builder()
            .documentNumber(request.documentNumber())
            .password(passwordEncoder.encode(request.password()))
            .roles(roles)
            .build();

        appUserRepository.save(user);

        var principal = User.withUsername(user.getDocumentNumber())
            .password(user.getPassword())
            .authorities(roles.stream().map(Enum::name).toArray(String[]::new))
            .build();

        var token = jwtService.generateToken(principal, Map.of("roles", roles));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.documentNumber(), request.password()));
        var user = appUserRepository.findByDocumentNumberIgnoreCase(request.documentNumber()).orElseThrow();
        var principal = User.withUsername(user.getDocumentNumber())
            .password(user.getPassword())
            .authorities(user.getRoles().stream().map(Enum::name).toArray(String[]::new))
            .build();
        var token = jwtService.generateToken(principal, Map.of("roles", user.getRoles()));
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds()));    
    }

}


