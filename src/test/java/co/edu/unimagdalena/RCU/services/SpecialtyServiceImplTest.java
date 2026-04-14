package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.SpecialtyDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.repositories.SpecialtyRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.exceptions.ResourceNotFoundException;
import co.edu.unimagdalena.RCU.services.implementation.SpecialtyServiceImpl;
import co.edu.unimagdalena.RCU.services.mapper.SpecialtyMapper;

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
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Spy
    private SpecialtyMapper specialtyMapper = Mappers.getMapper(SpecialtyMapper.class);

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    @Test
    void shouldCreateSpecialtySuccessfully() {
        // Given
        var request = new CreateSpecialtyRequest("Cardiology", "Heart specialist");
        when(specialtyRepository.existsByName(request.name())).thenReturn(false);
        when(specialtyRepository.save(any())).thenAnswer(inv -> {
            Specialty s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        // When
        var result = specialtyService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Cardiology");
        assertThat(result.description()).isEqualTo("Heart specialist");
        assertThat(result.id()).isNotNull();
        verify(specialtyRepository).save(any());
    }

    @Test
    void shouldThrowWhenSpecialtyNameAlreadyExists() {
        // Given
        var request = new CreateSpecialtyRequest("Cardiology", "Heart specialist");
        when(specialtyRepository.existsByName(request.name())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> specialtyService.create(request));
        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateRequestIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> specialtyService.create(null));
        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void shouldGetSpecialtyById() {
        // Given
        var id = UUID.randomUUID();
        var specialty = Specialty.builder()
                .id(id)
                .name("Cardiology")
                .description("Heart specialist")
                .active(true)
                .build();
        when(specialtyRepository.findById(id)).thenReturn(Optional.of(specialty));

        // When
        var result = specialtyService.getById(id);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Cardiology");
    }

    @Test
    void shouldThrowWhenSpecialtyNotFound() {
        // Given
        var id = UUID.randomUUID();
        when(specialtyRepository.findById(id)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> specialtyService.getById(id));
    }

    @Test
    void shouldGetAllSpecialties() {
        // Given
        var specialty = Specialty.builder()
                .id(UUID.randomUUID())
                .name("Cardiology")
                .description("Heart specialist")
                .active(true)
                .build();
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));

        // When
        var result = specialtyService.getAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Cardiology");
    }
}
