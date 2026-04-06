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
	@DisplayName("Patient: existsByEmail retorna true cuando el email existe")
	void shouldFindPatientByEmailWhenExists() {
		// given
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

		// when / then
		assertThat(patientRepository.existsByEmail("luis." + suffix + "@demo.com")).isTrue();
	}

	@Test
	@DisplayName("Patient: existsByEmail retorna false cuando el email no existe")
	void shouldNotFindPatientByEmailWhenMissing() {
		// when / then
		assertThat(patientRepository.existsByEmail("noexiste@demo.com")).isFalse();
	}

	@Test
	@DisplayName("Patient: existsByDocumentNumber retorna true cuando el documento existe")
	void shouldFindPatientByDocumentWhenExists() {
		// given
		String suffix = UUID.randomUUID().toString().substring(0, 8);

		Patient patient = Patient.builder()
				.firstName("Luis")
				.lastName("Gomez")
				.email("doc." + suffix + "@demo.com")
				.phone("3111111111")
				.documentType(DocumentType.CC)
				.documentNumber("PAC-" + suffix)
				.gender(Gender.MALE)
				.active(true)
				.createdAt(Instant.now())
				.build();
		patientRepository.save(patient);

		// when / then
		assertThat(patientRepository.existsByDocumentNumber("PAC-" + suffix)).isTrue();
	}

	@Test
	@DisplayName("Patient: existsByDocumentNumber retorna false cuando el documento no existe")
	void shouldNotFindPatientByDocumentWhenMissing() {
		// when / then
		assertThat(patientRepository.existsByDocumentNumber("PAC-X")).isFalse();
	}
}
