package co.edu.unimagdalena.RCU.mapper;

import org.mapstruct.Mapper;
import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {
    
    SpecialtyResponse toResponse(Specialty specialty);

    Specialty toEntity(CreateSpecialtyRequest request);

}
