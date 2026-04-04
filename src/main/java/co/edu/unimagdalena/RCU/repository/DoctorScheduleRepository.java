package co.edu.unimagdalena.RCU.repository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import co.edu.unimagdalena.RCU.entities.enums.DayOfWeek;
 
import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    // aca van los query, pero tengo que implementar algunos para las validaciones del service
    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek); // ORM - consulta si un doctor ya tiene un horario para un día específico

    List<DoctorSchedule> findByDoctorId(UUID doctorId); // ORM - consulta los horarios de un doctor por su ID

    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
}
