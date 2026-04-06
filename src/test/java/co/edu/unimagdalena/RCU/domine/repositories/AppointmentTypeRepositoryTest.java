package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;

class AppointmentTypeRepositoryTest extends AbstractRepositoryIT {

	@Autowired
	AppointmentTypeRepository appointmentTypeRepository;

	@Test
	@DisplayName("AppointmentType: existsByName retorna true cuando el nombre existe")
	void shouldFindAppointmentTypeByNameWhenExists() {
		// given
		AppointmentType type = AppointmentType.builder()
				.name("Control general")
				.description("Consulta de control")
				.active(true)
				.durationMinutes(30)
				.createdAt(Instant.now())
				.build();
		appointmentTypeRepository.save(type);

		// when
		boolean exists = appointmentTypeRepository.existsByName("Control general");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("AppointmentType: existsByName retorna false cuando el nombre no existe")
	void shouldNotFindAppointmentTypeByNameWhenMissing() {
		// when
		boolean notExists = appointmentTypeRepository.existsByName("No existe");

		// then
		assertThat(notExists).isFalse();
	}
}
