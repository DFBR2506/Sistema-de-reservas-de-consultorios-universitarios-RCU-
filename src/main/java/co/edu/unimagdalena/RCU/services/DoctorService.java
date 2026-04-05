package co.edu.unimagdalena.RCU.services;

import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.CreateDoctorRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.DoctorResponse;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.UpdateDoctorRequest;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest request);
    DoctorResponse getDoctorById(UUID id);
    List<DoctorResponse> getAllDoctors();
    DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest request);
}
