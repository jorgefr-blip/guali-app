package com.umg.guali.Controller;

import com.umg.guali.entity.Leccion;
import com.umg.guali.Repository.LeccionRepository;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/importar")
public class ImportarLeccionesController {

    private final LeccionRepository leccionRepository;

    public ImportarLeccionesController(LeccionRepository leccionRepository) {
        this.leccionRepository = leccionRepository;
    }

    @PostMapping("/lecciones")
    public String importarLecciones(@RequestParam("archivoZip") MultipartFile archivoZip, 
                                    RedirectAttributes redirectAttributes) {
        if (archivoZip.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El archivo está vacío.");
            return "redirect:/admin/lecciones";
        }

        // Creamos un archivo temporal en el disco
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("lecciones_", ".zip");
            archivoZip.transferTo(tempFile.toFile());

            try (ZipFile zipFile = new ZipFile(tempFile.toFile())) {
                Map<String, String> contenidosMd = new HashMap<>();
                Map<String, String> contenidosConfig = new HashMap<>();

                Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
                while (entries.hasMoreElements()) {
                    ZipArchiveEntry entry = entries.nextElement();
                    if (!entry.isDirectory()) {
                        String nombre = entry.getName();
                        String contenido = new String(zipFile.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
                        
                        String[] partes = nombre.split("/");
                        if (partes.length >= 2) {
                            String carpeta = partes[0];
                            
                            if (nombre.endsWith("leccion.md")) {
                                contenidosMd.put(carpeta, contenido);
                            } else if (nombre.endsWith("config.txt")) {
                                contenidosConfig.put(carpeta, contenido);
                            }
                        }
                    }
                }

                for (String carpeta : contenidosMd.keySet()) {
                    String md = contenidosMd.get(carpeta);
                    String config = contenidosConfig.getOrDefault(carpeta, "objetivo=EJECUTAR_EXITOSO");
                    
                    String objetivo = "EJECUTAR_EXITOSO";
                    if (config.contains("objetivo=")) {
                        objetivo = config.split("objetivo=")[1].trim();
                    }

                    Leccion leccion = new Leccion();
                    leccion.setTitulo(carpeta);
                    leccion.setContenido_md(md);
                    leccion.setObjetivo(objetivo);
                    leccion.setCreadaEn(LocalDateTime.now());
                    leccion.setActualizadaEn(LocalDateTime.now());
                    
                    leccionRepository.save(leccion);
                }
            }

            redirectAttributes.addFlashAttribute("exito", "Lecciones importadas correctamente.");
            return "redirect:/admin/lecciones";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al importar: " + e.getMessage());
            return "redirect:/admin/lecciones";
        } finally {
            // Borramos el archivo temporal después de usarlo
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}