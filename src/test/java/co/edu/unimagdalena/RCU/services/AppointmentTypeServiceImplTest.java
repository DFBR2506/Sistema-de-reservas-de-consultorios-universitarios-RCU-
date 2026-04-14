package co.edu.unimagdalena.RCU.services;

import co.edu.unimagdalena.RCU.api.dto.AppointmentTypeDtos.*;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;
import co.edu.unimagdalena.RCU.domine.repositories.AppointmentTypeRepository;
import co.edu.unimagdalena.RCU.exceptions.ConflictException;
import co.edu.unimagdalena.RCU.services.implementation.AppointmentTypeServiceImpl;
import co.edu.unimagdalena.RCU.services.mapper.AppointmentTypeMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentTypeServiceImplTest {

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Spy
    private AppointmentTypeMapper appointmentTypeMapper = Mappers.getMapper(AppointmentTypeMapper.class);

    @InjectMocks
    private AppointmentTypeServiceImpl appointmentTypeService;

    @Test
    void shouldCreateAppointmentTypeSuccessfully() {
        // Given
        var request = new CreateAppointmentTypeRequest("Consultation", "Regular medical consultation", 30);
        when(appointmentTypeRepository.existsByName(request.name())).thenReturn(false);
        when(appointmentTypeRepository.save(any())).thenAnswer(inv -> {
            AppointmentType at = inv.getArgument(0);
            at.setId(UUID.randomUUID());
            return at;
        });

        // When
        var result = appointmentTypeService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Consultation");
        assertThat(result.durationMinutes()).isEqualTo(30);
        verify(appointmentTypeRepository).save(any());
    }

    @Test
    void shouldThrowWhenAppointmentTypeNameAlreadyExists() {
        // Given
        var request = new CreateAppointmentTypeRequest("Consultation", "Regular medical consultation", 30);
        when(appointmentTypeRepository.existsByName(request.name())).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> appointmentTypeService.create(request));
        verify(appointmentTypeRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDurationIsNotPositive() {
        // Given
        var request = new CreateAppointmentTypeRequest("Consultation", "Regular medical consultation", 0);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> appointmentTypeService.create(request));
        verify(appointmentTypeRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCreateRequestIsNull() {
        // When / Then
        assertThrows(IllegalArgumentException.class, () -> appointmentTypeService.create(null));
        verify(appointmentTypeRepository, never()).save(any());
    }

    @Test
    void shouldGetAllAppointmentTypes() {
        // Given
        var appointmentType = AppointmentType.builder()
            .id(UUID.randomUUID())
            .name("Consultation")
            .description("Regular medical consultation")
            .durationMinutes(30)
            .active(true)
            .build();
        var pageable = Pageable.ofSize(10);
        when(appointmentTypeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(appointmentType), pageable, 1));

        // When
        var result = appointmentTypeService.getAll(pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Consultation");
    }
}
