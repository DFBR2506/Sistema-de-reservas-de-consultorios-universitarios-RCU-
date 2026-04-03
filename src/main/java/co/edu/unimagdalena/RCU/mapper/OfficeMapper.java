package co.edu.unimagdalena.RCU.mapper;
import org.mapstruct.Mapper;
import co.edu.unimagdalena.RCU.entities.*;
import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.*;

@Mapper(componentModel = "spring")
public interface OfficeMapper {
    OfficeResponse toResponse(Office office);

    Office toEntity(CreateOfficeRequest request);
}
