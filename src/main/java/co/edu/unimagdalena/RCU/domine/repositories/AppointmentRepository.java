package co.edu.unimagdalena.RCU.domine.repositories;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unimagdalena.RCU.domine.entities.Appointment;
import co.edu.unimagdalena.RCU.domine.entities.enums.Status;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    
    boolean existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(UUID doctorId, Instant endAt, Instant startAt); // ORM - consulta si un doctor tiene una cita que se solapa con un rango de tiempo específico

    boolean existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(UUID officeId, Instant endAt, Instant startAt); // ORM - consulta si un consultorio tiene una cita que se solapa con un rango de tiempo específico

    boolean existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(UUID patientId, Instant endAt, Instant startAt); // ORM - consulta si un paciente tiene una cita que se solapa con un rango de tiempo específico

    List<Appointment> findByPatientIdAndStatus(UUID patientId, Status status); // ORM - consulta las citas de un paciente por su ID y estado

    List<Appointment> findByDoctorIdAndStartAtBetween(UUID doctorId, Instant startAt, Instant endAt); // ORM - consulta las citas de un doctor por su ID y un rango de tiempo específico

    List<Appointment> findByOfficeIdAndStartAtBetween(UUID officeId, Instant startAt, Instant endAt); // ORM - consulta las citas de un consultorio por su ID y un rango de tiempo específico

    // Para ocupación de consultorios
    @Query(value = "SELECT a.office.id, a.office.code, COUNT(a) FROM Appointment a " +
        "WHERE a.startAt BETWEEN :startAt AND :endAt " +
        "GROUP BY a.office.id, a.office.code " +
        "ORDER BY COUNT(a) DESC",
        countQuery = "SELECT COUNT(DISTINCT a.office.id) FROM Appointment a " +
            "WHERE a.startAt BETWEEN :startAt AND :endAt")
    Page<Object[]> findOfficeOccupancy(@Param("startAt") Instant startAt, @Param("endAt") Instant endAt, Pageable pageable);

    // Para productividad de doctores
    @Query(value = "SELECT a.doctor.id, a.doctor.firstName, a.doctor.lastName, COUNT(a) FROM Appointment a " +
        "WHERE a.status = 'COMPLETED' " +
        "GROUP BY a.doctor.id, a.doctor.firstName, a.doctor.lastName " +
        "ORDER BY COUNT(a) DESC",
        countQuery = "SELECT COUNT(DISTINCT a.doctor.id) FROM Appointment a " +
            "WHERE a.status = 'COMPLETED'")
    Page<Object[]> findDoctorProductivity(Pageable pageable);

    // Para pacientes con más no-shows
    @Query(value = "SELECT a.patient.id, a.patient.firstName, a.patient.lastName, COUNT(a) FROM Appointment a " +
        "WHERE a.status = 'NO_SHOW' " +
        "AND a.startAt BETWEEN :startAt AND :endAt " +
        "GROUP BY a.patient.id, a.patient.firstName, a.patient.lastName " +
        "ORDER BY COUNT(a) DESC",
        countQuery = "SELECT COUNT(DISTINCT a.patient.id) FROM Appointment a " +
            "WHERE a.status = 'NO_SHOW' " +
            "AND a.startAt BETWEEN :startAt AND :endAt")
    Page<Object[]> findNoShowPatients(@Param("startAt") Instant startAt, @Param("endAt") Instant endAt, Pageable pageable);

}
