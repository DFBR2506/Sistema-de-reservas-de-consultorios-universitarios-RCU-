package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unimagdalena.RCU.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByEmail(String email); // ORM - consulta un doctor por su email
    boolean existsByDocumentNumber(String documentNumber); // ORM - consulta un doctor por su número de documento
    boolean existsByLicenseNumber(String licenseNumber); // ORM - consulta un doctor por su número de licencia médica
}
