package co.edu.unimagdalena.RCU.domine.repositories;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek); // ORM - consulta si un doctor ya tiene un horario para un día específico

    Page<DoctorSchedule> findByDoctorId(UUID doctorId, Pageable pageable); // ORM - consulta los horarios de un doctor por su ID

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
}
