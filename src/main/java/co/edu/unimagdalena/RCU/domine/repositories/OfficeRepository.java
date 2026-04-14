package co.edu.unimagdalena.RCU.domine.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.Office;

public interface OfficeRepository extends JpaRepository<Office, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByCode(String code); // ORM - consulta un consultorio por su código
}
