package co.edu.unimagdalena.RCU.domine.repositories;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.domine.entities.enums.DayOfWeek;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek); // ORM - consulta si un doctor ya tiene un horario para un día específico

    List<DoctorSchedule> findByDoctorId(UUID doctorId); // ORM - consulta los horarios de un doctor por su ID

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
}
