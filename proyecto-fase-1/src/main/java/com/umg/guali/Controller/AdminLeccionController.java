package com.umg.guali.Controller;

import com.umg.guali.entity.Leccion;
import com.umg.guali.Repository.LeccionRepository;
import com.umg.guali.Repository.IntentoHistorialRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/lecciones")
public class AdminLeccionController {

    private final LeccionRepository leccionRepository;
    private final IntentoHistorialRepository intentoHistorialRepository;

    // Constructor actualizado
    public AdminLeccionController(LeccionRepository leccionRepository, 
                                  IntentoHistorialRepository intentoHistorialRepository) {
        this.leccionRepository = leccionRepository;
        this.intentoHistorialRepository = intentoHistorialRepository;
    }

    @GetMapping
    public String listarLeccionesAdmin(Model model) {
        model.addAttribute("lecciones", leccionRepository.findAll());
        return "admin_lecciones"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("leccion", new Leccion());
        return "form_leccion";
    }

    @PostMapping("/guardar")
    public String guardarLeccion(@ModelAttribute Leccion leccion, Model model) {
        java.util.Set<String> objetivos = java.util.Set.of(
                "COMPILAR_CON_ERROR", "COMPILAR_EXITOSO", "EJECUTAR_CON_ERROR", "EJECUTAR_EXITOSO");
        if (leccion.getTitulo() == null || leccion.getTitulo().isBlank()
                || leccion.getContenido_md() == null || leccion.getContenido_md().isBlank()
                || !objetivos.contains(leccion.getObjetivo())) {
            model.addAttribute("error", "Completa el título, el contenido y elige un objetivo válido.");
            return "form_leccion";
        }
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        if (leccion.getId() == null) leccion.setCreadaEn(ahora);
        leccion.setActualizadaEn(ahora);
        leccionRepository.save(leccion);
        return "redirect:/admin/lecciones";
    }

    @PostMapping("/eliminar/{id}")
public String eliminarLeccion(@PathVariable Long id) {
    try {
        leccionRepository.deleteById(id);
    } catch (Exception e) {
        // Si falla (porque tiene historial), simplemente redirigimos
        // sin mostrar el error 500 en pantalla.
    }
    return "redirect:/admin/lecciones"; 
}
}