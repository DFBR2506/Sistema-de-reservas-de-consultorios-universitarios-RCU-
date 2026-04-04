package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    // aca van los query
}
