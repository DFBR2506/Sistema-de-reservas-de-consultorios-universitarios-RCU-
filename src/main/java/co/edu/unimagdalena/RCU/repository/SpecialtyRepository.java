package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByName(String name); // ORM - consulta una especialidad por su nombre
}
