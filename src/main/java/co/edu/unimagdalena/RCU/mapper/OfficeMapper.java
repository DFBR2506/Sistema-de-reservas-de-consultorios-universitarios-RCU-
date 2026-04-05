package co.edu.unimagdalena.RCU.mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.*;

@Mapper(componentModel = "spring")
public interface OfficeMapper {
    OfficeResponse toResponse(Office office);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Office toEntity(CreateOfficeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateOfficeRequest request, @MappingTarget Office office);
}
