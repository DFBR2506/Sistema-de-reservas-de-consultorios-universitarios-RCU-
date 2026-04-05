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
	@DisplayName("AppointmentType: existe por nombre")
	void shouldCheckExistsByName() {
		AppointmentType type = AppointmentType.builder()
				.name("Control general")
				.description("Consulta de control")
				.active(true)
				.durationMinutes(30)
				.createdAt(Instant.now())
				.build();
		appointmentTypeRepository.save(type);

		boolean exists = appointmentTypeRepository.existsByName("Control general");
		boolean notExists = appointmentTypeRepository.existsByName("No existe");

		assertThat(exists).isTrue();
		assertThat(notExists).isFalse();
	}
}
