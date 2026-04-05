package co.edu.unimagdalena.RCU.services.mappers;

import org.mapstruct.*;

import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.DoctorSchedule;

@Mapper(componentModel = "spring")
public interface DoctorScheduleMapper {
    DoctorScheduleResponse toResponse(DoctorSchedule doctorSchedule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DoctorSchedule toEntity(CreateDoctorScheduleRequest request);
}
