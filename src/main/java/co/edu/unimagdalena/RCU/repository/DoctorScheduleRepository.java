package co.edu.unimagdalena.RCU.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unimagdalena.RCU.entities.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    // aca van los query
}
