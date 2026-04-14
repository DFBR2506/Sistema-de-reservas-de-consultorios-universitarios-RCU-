package co.edu.unimagdalena.RCU.services;

import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.CreateDoctorRequest;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.DoctorResponse;
import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.UpdateDoctorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    DoctorResponse create(CreateDoctorRequest request);
    DoctorResponse getDoctorById(UUID id);
    Page<DoctorResponse> getAllDoctors(Pageable pageable);
    DoctorResponse updateDoctor(UUID id, UpdateDoctorRequest request);
}
