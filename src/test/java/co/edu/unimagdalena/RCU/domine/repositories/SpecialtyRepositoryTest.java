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
	@DisplayName("Specialty: existsByName retorna true cuando el nombre existe")
	void shouldFindSpecialtyByNameWhenExists() {
		// given
		Specialty specialty = Specialty.builder()
				.name("Cardiologia")
				.description("Especialidad del corazon")
				.active(true)
				.createdAt(Instant.now())
				.build();
		specialtyRepository.save(specialty);

		// when
		boolean exists = specialtyRepository.existsByName("Cardiologia");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Specialty: existsByName retorna false cuando el nombre no existe")
	void shouldNotFindSpecialtyByNameWhenMissing() {
		// when
		boolean notExists = specialtyRepository.existsByName("Neurologia");

		// then
		assertThat(notExists).isFalse();
	}
}
