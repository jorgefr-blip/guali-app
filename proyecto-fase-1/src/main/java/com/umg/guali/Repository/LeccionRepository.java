package com.umg.guali.Repository;

import com.umg.guali.entity.Leccion;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LeccionRepository extends JpaRepository<Leccion, Long> {
}