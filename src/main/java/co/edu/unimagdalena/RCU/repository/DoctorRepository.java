package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unimagdalena.RCU.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    // aca van los query
}
