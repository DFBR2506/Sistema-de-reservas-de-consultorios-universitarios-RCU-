package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.OfficeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Office;
import co.edu.unimagdalena.RCU.domine.repositories.OfficeRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.implementation.OfficeServiceImpl;
import co.edu.unimagdalena.RCU.services.mapper.OfficeMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceImplTest {

    @Mock
    private OfficeRepository officeRepository;

    @Spy
    private OfficeMapper officeMapper = Mappers.getMapper(OfficeMapper.class);

    @InjectMocks
    private OfficeServiceImpl officeService;

    @Test
    void shouldCreateOfficeSuccessfully() {
        // Given
        var request = new CreateOfficeRequest("CON-101", 3);
        when(officeRepository.existsByCode(request.code())).thenReturn(false);
        when(officeRepository.save(any())).thenAnswer(inv -> {
            Office o = inv.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        // When
        var result = officeService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("CON-101");
        assertThat(result.floor()).isEqualTo(3);
        verify(officeRepository).save(any());
    }

    @Test
    void shouldThrowWhenOfficeCodeAlreadyExists() {
        // Given
        var request = new CreateOfficeRequest("CON-101", 3);
        when(officeRepository.existsByCode(request.code())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> officeService.create(request));
        verify(officeRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateRequestIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> officeService.create(null));
        verify(officeRepository, never()).save(any());
    }

    @Test
    void shouldGetAllOffices() {
        // Given
        var office = Office.builder()
            .id(UUID.randomUUID())
            .code("CON-101")
            .floor(3)
            .active(true)
            .build();
        when(officeRepository.findAll()).thenReturn(List.of(office));

        // When
        var result = officeService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo("CON-101");
    }

    @Test
    void shouldUpdateOfficeSuccessfully() {
        // Given
        var id = UUID.randomUUID();
        var office = Office.builder()
            .id(id)
            .code("CON-101")
            .floor(3)
            .active(true)
            .build();
        var request = new UpdateOfficeRequest("CON-102", 4, false);
        when(officeRepository.findById(id)).thenReturn(Optional.of(office));
        when(officeRepository.existsByCode("CON-102")).thenReturn(false);
        when(officeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        var result = officeService.update(id, request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.code()).isEqualTo("CON-102");
        assertThat(result.floor()).isEqualTo(4);
        assertThat(result.active()).isEqualTo(false);
        verify(officeRepository).save(any());
    }

    @Test
    void shouldThrowWhenOfficeNotFoundForUpdate() {
        // Given
        var id = UUID.randomUUID();
        var request = new UpdateOfficeRequest("CON-102", 4, false);
        when(officeRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> officeService.update(id, request));
    }
}
