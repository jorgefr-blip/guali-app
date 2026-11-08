package com.umg.guali.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intento_historial")
public class IntentoHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "codigo_compilado", columnDefinition = "LONGTEXT")
    private String codigoCompilado;

    private String estado;

    @Column(name = "fecha_final")
    private LocalDateTime fechaFinal;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "leccion_titulo")
    private String leccionTitulo;

    private String objetivo;

    @Column(name = "ultima_actividad")
    private LocalDateTime ultimaActividad;

    @Column(name = "leccion_id")
    private Long leccionId;

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getCodigoCompilado() { return codigoCompilado; }
    public void setCodigoCompilado(String codigoCompilado) { this.codigoCompilado = codigoCompilado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDateTime fechaFinal) { this.fechaFinal = fechaFinal; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getLeccionTitulo() { return leccionTitulo; }
    public void setLeccionTitulo(String leccionTitulo) { this.leccionTitulo = leccionTitulo; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public LocalDateTime getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(LocalDateTime ultimaActividad) { this.ultimaActividad = ultimaActividad; }

    public Long getLeccionId() { return leccionId; }
    public void setLeccionId(Long leccionId) { this.leccionId = leccionId; }
}