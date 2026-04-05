package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Patient;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;

class PatientRepositoryTest extends AbstractRepositoryIT {

	@Autowired
	PatientRepository patientRepository;

	@Test
	@DisplayName("Patient: valida existencia por email y documento")
	void shouldCheckPatientUniqueFields() {
		String suffix = UUID.randomUUID().toString().substring(0, 8);

		Patient patient = Patient.builder()
				.firstName("Luis")
				.lastName("Gomez")
				.email("luis." + suffix + "@demo.com")
				.phone("3111111111")
				.documentType(DocumentType.CC)
				.documentNumber("PAC-" + suffix)
				.gender(Gender.MALE)
				.active(true)
				.createdAt(Instant.now())
				.build();
		patientRepository.save(patient);

		assertThat(patientRepository.existsByEmail("luis." + suffix + "@demo.com")).isTrue();
		assertThat(patientRepository.existsByDocumentNumber("PAC-" + suffix)).isTrue();

		assertThat(patientRepository.existsByEmail("noexiste@demo.com")).isFalse();
		assertThat(patientRepository.existsByDocumentNumber("PAC-X")).isFalse();
	}
}
