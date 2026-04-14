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
    // Valida traslape de citas para un doctor (MINIMO).
    boolean existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(UUID doctorId, Instant endAt, Instant startAt);

    // Valida traslape de citas para un consultorio (MINIMO).
    boolean existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(UUID officeId, Instant endAt, Instant startAt);

    // Valida traslape de citas para un paciente.
    boolean existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(UUID patientId, Instant endAt, Instant startAt);

    // Busca citas de un paciente por estado (MINIMO).
    List<Appointment> findByPatientIdAndStatus(UUID patientId, Status status);

    // Busca citas de un doctor en un rango de fecha/hora.
    List<Appointment> findByDoctorIdAndStartAtBetween(UUID doctorId, Instant startAt, Instant endAt);

    // Busca citas de un consultorio en un rango de fecha/hora.
    List<Appointment> findByOfficeIdAndStartAtBetween(UUID officeId, Instant startAt, Instant endAt);

    // Busca citas por rango de fecha/hora global (MINIMO).
    List<Appointment> findByStartAtBetween(Instant startAt, Instant endAt);

    // Obtiene los bloques de citas ya ocupados de un doctor en una fecha concreta (MINIMO).
    @Query("SELECT a FROM Appointment a " +
        "WHERE a.doctor.id = :doctorId " +
        "AND a.startAt >= :dayStart " +
        "AND a.startAt < :dayEnd " +
        "AND a.status <> 'CANCELLED' " +
        "ORDER BY a.startAt ASC")
    List<Appointment> findBookedSlotsByDoctorAndDate(
        @Param("doctorId") UUID doctorId,
        @Param("dayStart") Instant dayStart,
        @Param("dayEnd") Instant dayEnd
    );

    // Calcula ocupación de consultorios por rango (MINIMO).
    @Query(value = "SELECT a.office.id, a.office.code, COUNT(a) FROM Appointment a " +
        "WHERE a.startAt BETWEEN :startAt AND :endAt " +
        "GROUP BY a.office.id, a.office.code " +
        "ORDER BY COUNT(a) DESC",
        countQuery = "SELECT COUNT(DISTINCT a.office.id) FROM Appointment a " +
            "WHERE a.startAt BETWEEN :startAt AND :endAt")
    Page<Object[]> findOfficeOccupancy(@Param("startAt") Instant startAt, @Param("endAt") Instant endAt, Pageable pageable);

    // Calcula ocupación diaria de consultorios (MINIMO).
    @Query("SELECT a.office.id, a.office.code, COUNT(a) FROM Appointment a " +
        "WHERE a.startAt BETWEEN :dayStart AND :dayEnd " +
        "GROUP BY a.office.id, a.office.code " +
        "ORDER BY COUNT(a) DESC")
    List<Object[]> findOfficeDailyOccupancy(@Param("dayStart") Instant dayStart, @Param("dayEnd") Instant dayEnd);

    // Cuenta citas CANCELLED y NO_SHOW agrupadas por especialidad (MINIMO).
    @Query("SELECT a.doctor.specialty.id, a.doctor.specialty.name, " +
        "SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), " +
        "SUM(CASE WHEN a.status = 'NO_SHOW' THEN 1 ELSE 0 END) " +
        "FROM Appointment a " +
        "WHERE a.startAt BETWEEN :startAt AND :endAt " +
        "GROUP BY a.doctor.specialty.id, a.doctor.specialty.name " +
        "ORDER BY a.doctor.specialty.name ASC")
    List<Object[]> countCancelledAndNoShowBySpecialty(
        @Param("startAt") Instant startAt,
        @Param("endAt") Instant endAt
    );

    // Ranking de profesionales por citas completadas (MINIMO).
    @Query(value = "SELECT a.doctor.id, a.doctor.firstName, a.doctor.lastName, COUNT(a) FROM Appointment a " +
        "WHERE a.status = 'COMPLETED' " +
        "GROUP BY a.doctor.id, a.doctor.firstName, a.doctor.lastName " +
        "ORDER BY COUNT(a) DESC",
        countQuery = "SELECT COUNT(DISTINCT a.doctor.id) FROM Appointment a " +
            "WHERE a.status = 'COMPLETED'")
    Page<Object[]> findDoctorProductivity(Pageable pageable);

    // Pacientes con mayor número de NO_SHOW en un período (MINIMO).
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
