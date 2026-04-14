package co.edu.unimagdalena.RCU.services;

import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.CreatePatientRequest;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.PatientResponse;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.UpdatePatientRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PatientService {
    PatientResponse create(CreatePatientRequest request);
    PatientResponse getById(UUID id);
    Page<PatientResponse> getAll(Pageable pageable);
    PatientResponse update(UUID id, UpdatePatientRequest request);
}
