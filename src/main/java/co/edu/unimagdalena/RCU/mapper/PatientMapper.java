package co.edu.unimagdalena.RCU.mapper;
import org.mapstruct.Mapper;
import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.PatientDtos.*;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponse toResponse(Patient patient);

    Patient toEntity(CreatePatientRequest request);
} 