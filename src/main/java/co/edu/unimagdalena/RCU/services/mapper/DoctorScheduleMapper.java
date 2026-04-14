package co.edu.unimagdalena.RCU.mapper;

import org.mapstruct.*;

import co.edu.unimagdalena.RCU.entities.DoctorSchedule;
import co.edu.unimagdalena.RCU.api.dto.DoctorScheduleDtos.*;

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
