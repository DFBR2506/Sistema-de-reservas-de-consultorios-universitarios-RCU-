package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Office;

class OfficeRepositoryTest extends AbstractRepositoryIT {

	@Autowired
	OfficeRepository officeRepository;

	@Test
	@DisplayName("Office: existsByCode retorna true cuando el código existe")
	void shouldFindOfficeByCodeWhenExists() {
		// given
		Office office = Office.builder()
				.code("A-101")
				.floor(1)
				.active(true)
				.createdAt(Instant.now())
				.build();
		officeRepository.save(office);

		// when
		boolean exists = officeRepository.existsByCode("A-101");

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("Office: existsByCode retorna false cuando el código no existe")
	void shouldNotFindOfficeByCodeWhenMissing() {
		// when
		boolean notExists = officeRepository.existsByCode("B-202");

		// then
		assertThat(notExists).isFalse();
	}
}
