package com.umg.guali.Controller; // O 'controller' si lo tienes en minúscula

import com.umg.guali.Repository.IntentoHistorialRepository;
import com.umg.guali.Repository.LeccionRepository;
import com.umg.guali.Repository.UsuarioAdminRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/estadisticas")
public class AdminEstadisticasController {

    private final IntentoHistorialRepository intentoHistorialRepository;
    private final LeccionRepository leccionRepository;
    private final UsuarioAdminRepository usuarioAdminRepository;

    public AdminEstadisticasController(IntentoHistorialRepository intentoHistorialRepository,
                                       LeccionRepository leccionRepository,
                                       UsuarioAdminRepository usuarioAdminRepository) {
        this.intentoHistorialRepository = intentoHistorialRepository;
        this.leccionRepository = leccionRepository;
        this.usuarioAdminRepository = usuarioAdminRepository;
    }

    @GetMapping
    public String verEstadisticas(
            @RequestParam(value = "desde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        // Conteos generales
        long totalLecciones = leccionRepository.count();
        long totalUsuarios = usuarioAdminRepository.count();
        
        // Conteo de intentos (con o sin filtro)
        long totalIntentos;
        if (desde != null && hasta != null) {
            LocalDateTime inicio = desde.atStartOfDay();
            LocalDateTime fin = hasta.atTime(LocalTime.MAX);
            totalIntentos = intentoHistorialRepository.countByFechaInicioBetween(inicio, fin);
            model.addAttribute("desde", desde);
            model.addAttribute("hasta", hasta);
        } else {
            totalIntentos = intentoHistorialRepository.count();
        }

        model.addAttribute("totalLecciones", totalLecciones);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalIntentos", totalIntentos);

        return "admin_estadisticas";
    }
}