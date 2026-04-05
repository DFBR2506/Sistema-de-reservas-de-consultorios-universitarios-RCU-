package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;

class DoctorRepositoryTest extends AbstractRepositoryIT {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	SpecialtyRepository specialtyRepository;

	@Test
	@DisplayName("Doctor: valida existencia por email, documento y licencia")
	void shouldCheckDoctorUniqueFields() {
		String suffix = UUID.randomUUID().toString().substring(0, 8);

		Specialty specialty = specialtyRepository.save(Specialty.builder()
				.name("Pediatria-" + suffix)
				.description("Especialidad pediatrica")
				.active(true)
				.createdAt(Instant.now())
				.build());

		Doctor doctor = Doctor.builder()
				.firstName("Ana")
				.lastName("Perez")
				.email("ana." + suffix + "@demo.com")
				.phone("3000000000")
				.documentType(DocumentType.CC)
				.documentNumber("DOC-" + suffix)
				.gender(Gender.FEMALE)
				.active(true)
				.createdAt(Instant.now())
				.licenseNumber("LIC-" + suffix)
				.specialty(specialty)
				.build();
		doctorRepository.save(doctor);

		assertThat(doctorRepository.existsByEmail("ana." + suffix + "@demo.com")).isTrue();
		assertThat(doctorRepository.existsByDocumentNumber("DOC-" + suffix)).isTrue();
		assertThat(doctorRepository.existsByLicenseNumber("LIC-" + suffix)).isTrue();

		assertThat(doctorRepository.existsByEmail("noexiste@demo.com")).isFalse();
		assertThat(doctorRepository.existsByDocumentNumber("DOC-X")).isFalse();
		assertThat(doctorRepository.existsByLicenseNumber("LIC-X")).isFalse();
	}
}
