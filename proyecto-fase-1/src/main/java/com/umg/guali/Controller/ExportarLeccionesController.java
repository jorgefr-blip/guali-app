package com.umg.guali.Controller;

import com.umg.guali.entity.Leccion;
import com.umg.guali.Repository.LeccionRepository;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin/exportar")
public class ExportarLeccionesController {

    private final LeccionRepository leccionRepository;

    public ExportarLeccionesController(LeccionRepository leccionRepository) {
        this.leccionRepository = leccionRepository;
    }

    @GetMapping("/lecciones")
    public ResponseEntity<byte[]> exportarLecciones() {
        try {
            List<Leccion> lecciones = leccionRepository.findAll();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos);

            for (Leccion leccion : lecciones) {
                String nombreCarpeta = leccion.getTitulo() + "/";

                // 1. Archivo leccion.md
                ZipArchiveEntry entryMd = new ZipArchiveEntry(nombreCarpeta + "leccion.md");
                zos.putArchiveEntry(entryMd);
                String contenidoMd = leccion.getContenido_md() != null ? leccion.getContenido_md() : "";
                zos.write(contenidoMd.getBytes(StandardCharsets.UTF_8));
                zos.closeArchiveEntry();

                // 2. Archivo config.txt
                ZipArchiveEntry entryConfig = new ZipArchiveEntry(nombreCarpeta + "config.txt");
                zos.putArchiveEntry(entryConfig);
                String contenidoConfig = "objetivo=" + leccion.getObjetivo();
                zos.write(contenidoConfig.getBytes(StandardCharsets.UTF_8));
                zos.closeArchiveEntry();
            }

            zos.finish();
            zos.close();

            byte[] zipBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "lecciones.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}