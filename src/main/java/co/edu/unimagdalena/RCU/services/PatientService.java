package co.edu.unimagdalena.RCU.service;

import java.util.List;
import java.util.UUID;

import co.edu.unimagdalena.RCU.api.dto.PatientDtos.CreatePatientRequest;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.PatientResponse;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.UpdatePatientRequest;



public interface PatientService {
    PatientResponse create(CreatePatientRequest request);
    PatientResponse getById(UUID id);
    List<PatientResponse> getAll();
    PatientResponse update(UUID id, UpdatePatientRequest request);
}
