package com.umg.guali.Controller;

import com.umg.guali.entity.Leccion;
import com.umg.guali.entity.IntentoHistorial;
import com.umg.guali.Repository.LeccionRepository;
import com.umg.guali.Repository.IntentoHistorialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;

@Controller
public class LeccionController {

    private final LeccionRepository leccionRepository;
    private final IntentoHistorialRepository intentoHistorialRepository;

    public LeccionController(LeccionRepository leccionRepository, 
                             IntentoHistorialRepository intentoHistorialRepository) {
        this.leccionRepository = leccionRepository;
        this.intentoHistorialRepository = intentoHistorialRepository;
    }

    @GetMapping("/lecciones")
    public String listarLecciones(Model model) {
        model.addAttribute("lecciones", leccionRepository.findAll());
        return "lecciones"; 
    }

    @GetMapping("/lecciones/{id}")
    public String abrirLeccion(@PathVariable Long id, Model model) {
        Leccion leccionActiva = leccionRepository.findById(id).orElse(null);
        
        if (leccionActiva != null) {
            // Guardar en el historial
            IntentoHistorial intento = new IntentoHistorial();
            intento.setLeccionId(leccionActiva.getId());
            intento.setLeccionTitulo(leccionActiva.getTitulo());
            
            LocalDateTime ahora = LocalDateTime.now();
            intento.setFechaInicio(ahora);
            intento.setUltimaActividad(ahora);
            
            intento.setEstado("PENDIENTE");
            intento.setObjetivo(leccionActiva.getObjetivo());
            intento.setClientId("estudiante-web"); 
            
            intentoHistorialRepository.save(intento);
        }

        model.addAttribute("lecciones", leccionRepository.findAll());
        model.addAttribute("leccionActiva", leccionActiva);
        return "lecciones"; 
    }
}