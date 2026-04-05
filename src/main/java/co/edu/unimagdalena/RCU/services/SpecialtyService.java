package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import java.util.List;
import java.util.UUID;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest request);
    SpecialtyResponse getById(UUID id);
    List<SpecialtyResponse> getAll();
}
