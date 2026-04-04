package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByEmail(String email); // ORM - consulta un paciente por su email
    boolean existsByDocumentNumber(String documentNumber); // ORM - consulta un paciente por su número de documento
}
