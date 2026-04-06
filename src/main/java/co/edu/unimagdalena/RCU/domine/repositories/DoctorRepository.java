package co.edu.unimagdalena.RCU.domine.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    // Busca doctores activos por especialidad (MINIMO).
    List<Doctor> findBySpecialtyIdAndActiveTrue(UUID specialtyId);

    // Validaciones de unicidad para creación/actualización de doctor.
    boolean existsByEmail(String email); // ORM - consulta un doctor por su email
    boolean existsByDocumentNumber(String documentNumber); // ORM - consulta un doctor por su número de documento
    boolean existsByLicenseNumber(String licenseNumber); // ORM - consulta un doctor por su número de licencia médica
}
