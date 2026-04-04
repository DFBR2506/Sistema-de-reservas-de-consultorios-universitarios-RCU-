package co.edu.unimagdalena.RCU.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponse toResponse(Specialty specialty);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "doctors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Specialty toEntity(CreateSpecialtyRequest request);

}
