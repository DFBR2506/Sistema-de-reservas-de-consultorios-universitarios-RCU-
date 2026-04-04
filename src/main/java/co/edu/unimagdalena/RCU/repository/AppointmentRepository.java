package co.edu.unimagdalena.RCU.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.Appointment;
import co.edu.unimagdalena.RCU.entities.enums.Status;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    
    boolean existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(UUID doctorId, Instant endAt, Instant startAt); // ORM - consulta si un doctor tiene una cita que se solapa con un rango de tiempo específico

    boolean existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(UUID officeId, Instant endAt, Instant startAt); // ORM - consulta si un consultorio tiene una cita que se solapa con un rango de tiempo específico

    boolean existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(UUID patientId, Instant endAt, Instant startAt); // ORM - consulta si un paciente tiene una cita que se solapa con un rango de tiempo específico

    List<Appointment> findByPatientIdAndStatus(UUID patientId, Status status); // ORM - consulta las citas de un paciente por su ID y estado

    List<Appointment> findByDoctorIdAndStartAtBetween(UUID doctorId, Instant startAt, Instant endAt); // ORM - consulta las citas de un doctor por su ID y un rango de tiempo específico

    List<Appointment> findByOfficeIdAndStartAtBetween(UUID officeId, Instant startAt, Instant endAt); // ORM - consulta las citas de un consultorio por su ID y un rango de tiempo específico

}
