package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    // aca van los query
}
