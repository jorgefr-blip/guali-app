package com.umg.guali.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "leccion")
public class Leccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(name = "contenido_md", columnDefinition = "LONGTEXT")
    private String contenido_md; 

    private String objetivo; 

    @Column(name = "creada_en")
    private LocalDateTime creadaEn;

    @Column(name = "actualizada_en")
    private LocalDateTime actualizadaEn;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido_md() { return contenido_md; }
    public void setContenido_md(String contenido_md) { this.contenido_md = contenido_md; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public LocalDateTime getCreadaEn() { return creadaEn; }
    public void setCreadaEn(LocalDateTime creadaEn) { this.creadaEn = creadaEn; }

    public LocalDateTime getActualizadaEn() { return actualizadaEn; }
    public void setActualizadaEn(LocalDateTime actualizadaEn) { this.actualizadaEn = actualizadaEn; }
}