package com.ecommerce.infrastructure.config;

import com.ecommerce.domain.port.output.TokenGeneratorPort;
import com.ecommerce.domain.port.output.UserRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ADAPTATEUR - Configuration Spring Security.
 * Configure JWT, CORS et les règles d'autorisation.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/api/products").permitAll()
                .requestMatchers("/api/products/{id}").permitAll()
                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(fo -> fo.disable())) // H2 console
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Filtre JWT injecté dans la chaîne Spring Security.
     */
    @Component
    @RequiredArgsConstructor
    @Slf4j
    static class JwtAuthFilter extends OncePerRequestFilter {

        private final TokenGeneratorPort tokenGenerator;
        private final UserRepositoryPort userRepository;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {
            String token = extractToken(request);
            if (StringUtils.hasText(token) && tokenGenerator.validateToken(token)) {
                String username = tokenGenerator.extractUsername(token);
                userRepository.findByUsername(username).ifPresent(user -> {
                    Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toSet());
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });
            }
            chain.doFilter(request, response);
        }

        private String extractToken(HttpServletRequest request) {
            String header = request.getHeader("Authorization");
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                return header.substring(7);
            }
            return null;
        }
    }
}
