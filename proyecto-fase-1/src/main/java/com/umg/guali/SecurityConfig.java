package com.umg.guali;

import com.umg.guali.Repository.UsuarioAdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioAdminRepository usuarioAdminRepository;

    public SecurityConfig(UsuarioAdminRepository usuarioAdminRepository) {
        this.usuarioAdminRepository = usuarioAdminRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF activo para todo el sitio (los formularios Thymeleaf con th:action llevan el token).
            // Solo se excluye /api/**: son endpoints publicos, sin sesion, que usa el estudiante (compilar/ejecutar).
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

            // Sin cache de peticion: evita el parametro "?continue" que Spring Security 6.5 agrega
            // al volver de /login. Despues de iniciar sesion siempre se va a /admin/panel.
            .requestCache(cache -> cache.requestCache(new NullRequestCache()))

            .authorizeHttpRequests(auth -> auth
                // TODO lo administrativo exige rol ADMIN: panel, usuarios, lecciones,
                // importar, exportar y estadisticas.
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // El estudiante no necesita autenticarse (inicio, lecciones, historial, API de compilacion, css, js).
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/panel")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/lecciones")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioAdminRepository.findByUsername(username)
            .map(u -> User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles("ADMIN")
                .disabled(u.getActivo() != null && !u.getActivo())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}