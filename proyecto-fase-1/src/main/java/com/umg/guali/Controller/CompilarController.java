package com.umg.guali.Controller;

import com.umg.guali.entity.IntentoHistorial;
import com.umg.guali.entity.Leccion;
import com.umg.guali.Repository.IntentoHistorialRepository;
import com.umg.guali.Repository.LeccionRepository;
import edu.umg.guali.compiler.GualiDev;
import edu.umg.guali.compiler.CompileResult;
import edu.umg.guali.compiler.ExecuteResult;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class CompilarController {

    private final String JDK_HOME = "C:/Program Files/Eclipse Adoptium/jdk-25.0.5.8-hotspot";

    private final IntentoHistorialRepository intentoHistorialRepository;
    private final LeccionRepository leccionRepository;

    // Constructor con inyección de dependencias
    public CompilarController(IntentoHistorialRepository intentoHistorialRepository, 
                              LeccionRepository leccionRepository) {
        this.intentoHistorialRepository = intentoHistorialRepository;
        this.leccionRepository = leccionRepository;
    }

    @PostMapping("/compilar")
    public Map<String, String> compilar(@RequestBody Map<String, String> request) {
        String codigo = request.get("codigo");
        Map<String, String> respuesta = new HashMap<>();

        try {
            CompileResult resultado = GualiDev.compile(codigo, JDK_HOME);
            
            if (resultado.isExitoso()) {
                respuesta.put("salida", "¡Compilación exitosa!");
                respuesta.put("exito", "true");
                respuesta.put("nombreClase", resultado.getNombreClase());
            } else {
                respuesta.put("salida", resultado.getSalida());
                respuesta.put("exito", "false");
            }
        } catch (Exception e) {
            respuesta.put("salida", "Error inesperado: " + e.getMessage());
            respuesta.put("exito", "false");
        }
        return respuesta;
    }


    @PostMapping("/verificar-objetivo/{leccionId}/{resultado}")
public Map<String, String> verificarObjetivoManual(@PathVariable Long leccionId, @PathVariable String resultado) {
    verificarObjetivo(leccionId, resultado);
    Map<String, String> respuesta = new HashMap<>();
    respuesta.put("mensaje", "Verificación ejecutada");
    return respuesta;
}

    @PostMapping("/ejecutar")
    public Map<String, String> ejecutar(@RequestBody Map<String, String> request) {
        String codigo = request.get("codigo");
        // Obtenemos el ID de la lección que viene desde el frontend
        Long leccionId = Long.parseLong(request.getOrDefault("leccionId", "0"));
        
        Map<String, String> respuesta = new HashMap<>();

        try {
            CompileResult compilado = GualiDev.compile(codigo, JDK_HOME);
            
            if (compilado.isExitoso()) {
                ExecuteResult ejecucion = GualiDev.execute(compilado.getNombreClase(), JDK_HOME);
                
                if (ejecucion.isTimeoutAlcanzado()) {
                    respuesta.put("salida", "Error: Tiempo de ejecución excedido (Timeout).");
                } else if (ejecucion.isExitoso()) {
                    respuesta.put("salida", ejecucion.getSalida());
                    // Si ejecutó bien, verificamos si el objetivo es EJECUTAR_EXITOSO
                    verificarObjetivo(leccionId, "EJECUTAR_EXITOSO");
                } else {
                    respuesta.put("salida", ejecucion.getErrores());
                    // Si falló la ejecución, verificamos si el objetivo es EJECUTAR_CON_ERROR
                    verificarObjetivo(leccionId, "EJECUTAR_CON_ERROR");
                }
            } else {
                respuesta.put("salida", compilado.getSalida());
                // Si falló la compilación, verificamos si el objetivo es COMPILAR_CON_ERROR
                verificarObjetivo(leccionId, "COMPILAR_CON_ERROR");
            }
        } catch (Exception e) {
            respuesta.put("salida", "Error inesperado: " + e.getMessage());
        }
        return respuesta;
    }

    // Método para verificar y marcar el intento como COMPLETADA
    private void verificarObjetivo(Long leccionId, String resultadoAccion) {
        if (leccionId == 0) return;

        Leccion leccion = leccionRepository.findById(leccionId).orElse(null);
        if (leccion == null) return;

        // Si el objetivo de la lección coincide con el resultado de la acción
        if (leccion.getObjetivo().equals(resultadoAccion) || 
            (leccion.getObjetivo().equals("COMPILAR_EXITOSO") && resultadoAccion.equals("EJECUTAR_EXITOSO"))) {
            
            // Buscamos el intento más reciente de esta lección que esté PENDIENTE
            List<IntentoHistorial> intentos = intentoHistorialRepository.findAll();
            IntentoHistorial ultimoIntento = null;
            for (IntentoHistorial i : intentos) {
                if (i.getLeccionId().equals(leccionId) && "PENDIENTE".equals(i.getEstado())) {
                    ultimoIntento = i; // Nos quedamos con el último que encontremos
                }
            }

            if (ultimoIntento != null) {
                ultimoIntento.setEstado("COMPLETADA");
                ultimoIntento.setFechaFinal(LocalDateTime.now());
                intentoHistorialRepository.save(ultimoIntento);
            }
        }
    }
}