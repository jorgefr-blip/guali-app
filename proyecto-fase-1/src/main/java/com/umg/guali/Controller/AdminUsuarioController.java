package com.umg.guali.Controller; 

import com.umg.guali.entity.UsuarioAdmin;
import com.umg.guali.Repository.UsuarioAdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioAdminRepository usuarioAdminRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminUsuarioController(UsuarioAdminRepository usuarioAdminRepository) {
        this.usuarioAdminRepository = usuarioAdminRepository;
    }

    // 1. Mostrar la lista de usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioAdminRepository.findAll());
        return "admin_usuarios"; 
    }

    // 2. Mostrar el formulario para crear un usuario
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new UsuarioAdmin());
        return "form_usuario";
    }

    // 3. Guardar (crear o editar) el usuario
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute UsuarioAdmin usuario, 
                                 @RequestParam(value = "password_plano", required = false) String passwordPlano) {
        
        if (usuario.getId() == null) {
            // Es un usuario nuevo
            usuario.setCreadoEn(LocalDateTime.now());
            usuario.setActivo(true);
            if (passwordPlano != null && !passwordPlano.isEmpty()) {
                usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));
            }
        } else {
            // Es una edición
            UsuarioAdmin existente = usuarioAdminRepository.findById(usuario.getId()).orElse(null);
            if (existente != null) {
                if (passwordPlano != null && !passwordPlano.isEmpty()) {
                    usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));
                } else {
                    usuario.setPasswordHash(existente.getPasswordHash());
                }
                usuario.setCreadoEn(existente.getCreadoEn());
            }
        }
        
        usuarioAdminRepository.save(usuario);
        return "redirect:/admin/usuarios";
    }

    // 4. Eliminar un usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioAdminRepository.deleteById(id);
        return "redirect:/admin/usuarios";
    }
}