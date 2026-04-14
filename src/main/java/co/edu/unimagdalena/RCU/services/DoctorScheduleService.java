package co.edu.unimagdalena.RCU.services;

import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(UUID doctorId, CreateDoctorScheduleRequest request);
    Page<DoctorScheduleResponse> getAllSchedules(UUID doctorId, Pageable pageable);
}
