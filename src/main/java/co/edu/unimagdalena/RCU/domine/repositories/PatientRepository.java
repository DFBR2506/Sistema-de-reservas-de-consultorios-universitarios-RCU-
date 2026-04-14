package co.edu.unimagdalena.RCU.domine.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email); // ORM - consulta un paciente por su email
    boolean existsByDocumentNumber(String documentNumber); // ORM - consulta un paciente por su número de documento
}
