package com.umg.guali.Controller;

import com.umg.guali.Repository.IntentoHistorialRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistorialController {

    private final IntentoHistorialRepository intentoHistorialRepository;

    public HistorialController(IntentoHistorialRepository intentoHistorialRepository) {
        this.intentoHistorialRepository = intentoHistorialRepository;
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        // Traemos todos los registros, ordenados por ID descendente (el más nuevo primero)
        model.addAttribute("historial", intentoHistorialRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "historial"; 
    }
}