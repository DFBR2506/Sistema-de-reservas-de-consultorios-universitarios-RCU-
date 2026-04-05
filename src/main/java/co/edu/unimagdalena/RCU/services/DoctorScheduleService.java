package co.edu.unimagdalena.RCU.services;

import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.CreateDoctorScheduleRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.DoctorScheduleResponse;

public interface DoctorScheduleService {
    DoctorScheduleResponse create(UUID doctorId, CreateDoctorScheduleRequest request);
    List<DoctorScheduleResponse> getAllSchedules(UUID doctorId);
}
