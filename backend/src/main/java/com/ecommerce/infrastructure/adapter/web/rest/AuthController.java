package com.ecommerce.infrastructure.adapter.web.rest;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.port.input.AuthenticationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * ADAPTATEUR WEB - API REST pour l'authentification.
 *
 * Traduit les requêtes HTTP en appels aux ports d'entrée du domaine.
 * Ne contient aucune logique métier.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentification", description = "Endpoints d'authentification")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationUseCase.AuthResult result = authenticationUseCase.login(
                request.username(), request.password()
        );
        return ResponseEntity.ok(new LoginResponse(
                result.token(),
                result.user().getId(),
                result.user().getUsername(),
                result.user().getRoles(),
                result.expiresIn()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription utilisateur")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .roles(Set.of("USER"))
                .build();

        User registered = authenticationUseCase.register(newUser);
        return ResponseEntity.ok(new UserResponse(
                registered.getId(),
                registered.getUsername(),
                registered.getEmail(),
                registered.getRoles()
        ));
    }

    // DTOs (records Java - immuables, sans boilerplate)
    public record LoginRequest(
            @NotBlank(message = "Le nom d'utilisateur est requis") String username,
            @NotBlank(message = "Le mot de passe est requis") String password
    ) {}

    public record RegisterRequest(
            @NotBlank String username,
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String token,
            String userId,
            String username,
            Set<String> roles,
            long expiresIn
    ) {}

    public record UserResponse(
            String id,
            String username,
            String email,
            Set<String> roles
    ) {}
}
