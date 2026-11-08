package com.umg.guali;

import com.umg.guali.Repository.UsuarioAdminRepository;
import com.umg.guali.entity.UsuarioAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tu tabla usuario_admin tenia la contrasena en texto plano (ej. "admin123") porque antes
 * se usaba NoOpPasswordEncoder. Ahora que se usa BCrypt, al arrancar convierte automaticamente
 * cualquier contrasena que NO sea un hash BCrypt (los hashes empiezan con "$2"). Es idempotente:
 * si ya estan en BCrypt no toca nada. Despues podras entrar con la misma contrasena de siempre.
 */
@Component
public class MigradorContrasenas implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MigradorContrasenas.class);

    private final UsuarioAdminRepository usuarios;
    private final PasswordEncoder encoder;

    public MigradorContrasenas(UsuarioAdminRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (UsuarioAdmin u : usuarios.findAll()) {
            String actual = u.getPasswordHash();
            if (actual != null && !actual.startsWith("$2")) {
                u.setPasswordHash(encoder.encode(actual));
                usuarios.save(u);
                log.info("Contraseña del administrador '{}' convertida a BCrypt.", u.getUsername());
            }
        }
    }
}
