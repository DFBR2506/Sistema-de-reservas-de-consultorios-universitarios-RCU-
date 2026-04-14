package co.edu.unimagdalena.RCU.services.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.*;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponse toResponse(Specialty specialty);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(CreateSpecialtyRequest request);

}
