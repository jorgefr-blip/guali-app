package com.umg.guali.Repository;

import com.umg.guali.entity.IntentoHistorial;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface IntentoHistorialRepository extends JpaRepository<IntentoHistorial, Long> {
    
    // Método para borrar todos los intentos de una lección específica
    @Transactional
    void deleteByLeccionId(Long leccionId);
    long countByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
}