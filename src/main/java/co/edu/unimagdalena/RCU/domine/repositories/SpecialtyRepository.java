package co.edu.unimagdalena.RCU.domine.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {
    boolean existsByName(String name); // ORM - consulta una especialidad por su nombre
}
