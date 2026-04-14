package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecialtyService {
    SpecialtyResponse create(CreateSpecialtyRequest request);
    SpecialtyResponse getById(UUID id);
    Page<SpecialtyResponse> getAll(Pageable pageable);
}
