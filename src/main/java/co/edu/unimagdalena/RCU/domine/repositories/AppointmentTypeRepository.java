package co.edu.unimagdalena.RCU.domine.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByName(String name); // ORM - consulta un tipo de cita por su nombre
}
