package co.edu.unimagdalena.RCU.services.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import co.edu.unimagdalena.RCU.api.dto.DoctorDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.*;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(source = "specialty.id", target = "specialtyId")
    DoctorResponse toResponse(Doctor doctor);

    @Mapping(target = "specialty", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor toEntity(CreateDoctorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "specialty", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateDoctorRequest request, @MappingTarget Doctor doctor);

}
