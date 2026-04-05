package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;

class SpecialtyRepositoryTest extends AbstractRepositoryIT {

	@Autowired
	SpecialtyRepository specialtyRepository;

	@Test
	@DisplayName("Specialty: existe por nombre")
	void shouldCheckExistsByName() {
		Specialty specialty = Specialty.builder()
				.name("Cardiologia")
				.description("Especialidad del corazon")
				.active(true)
				.createdAt(Instant.now())
				.build();
		specialtyRepository.save(specialty);

		boolean exists = specialtyRepository.existsByName("Cardiologia");
		boolean notExists = specialtyRepository.existsByName("Neurologia");

		assertThat(exists).isTrue();
		assertThat(notExists).isFalse();
	}
}
