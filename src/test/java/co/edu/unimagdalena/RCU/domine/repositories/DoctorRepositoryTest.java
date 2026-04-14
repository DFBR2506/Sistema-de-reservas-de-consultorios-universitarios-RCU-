package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
		// given
		String suffix = UUID.randomUUID().toString().substring(0, 8);
		Specialty specialty = createSpecialty("Pediatria-" + suffix);
		createDoctor("Ana", "Perez", "ana." + suffix + "@demo.com", "DOC-" + suffix, "LIC-" + suffix, true, specialty);

		// when / then
		assertThat(doctorRepository.existsByEmail("ana." + suffix + "@demo.com")).isTrue();
		assertThat(doctorRepository.existsByDocumentNumber("DOC-" + suffix)).isTrue();
		assertThat(doctorRepository.existsByLicenseNumber("LIC-" + suffix)).isTrue();

		assertThat(doctorRepository.existsByEmail("noexiste@demo.com")).isFalse();
		assertThat(doctorRepository.existsByDocumentNumber("DOC-X")).isFalse();
		assertThat(doctorRepository.existsByLicenseNumber("LIC-X")).isFalse();
	}

	@Test
	@DisplayName("Doctor: retorna solo activos por especialidad")
	void shouldReturnOnlyActiveDoctorsBySpecialty() {
		// given
		String suffix = UUID.randomUUID().toString().substring(0, 8);
		Specialty specialty = createSpecialty("Pediatria-" + suffix);
		Specialty otherSpecialty = createSpecialty("Dermatologia-" + suffix);

		createDoctor("Ana", "Activa", "ana." + suffix + "@demo.com", "DOC-" + suffix, "LIC-" + suffix, true, specialty);
		createDoctor("Inactivo", "MismoEspecialidad", "inactive." + suffix + "@demo.com", "DOC-INACT-" + suffix,
				"LIC-INACT-" + suffix, false, specialty);
		createDoctor("Activo", "OtraEspecialidad", "other." + suffix + "@demo.com", "DOC-OTHER-" + suffix,
				"LIC-OTHER-" + suffix, true, otherSpecialty);

		// when
		List<Doctor> activeBySpecialty = doctorRepository.findBySpecialtyIdAndActiveTrue(specialty.getId(), Pageable.unpaged())
				.getContent();

		// then
		assertThat(activeBySpecialty).hasSize(1);
		assertThat(activeBySpecialty.get(0).getEmail()).isEqualTo("ana." + suffix + "@demo.com");
	}

	@Test
	@DisplayName("Doctor: retorna vacio cuando no hay activos para la especialidad")
	void shouldReturnEmptyWhenSpecialtyHasNoActiveDoctors() {
		// given
		String suffix = UUID.randomUUID().toString().substring(0, 8);
		Specialty specialty = createSpecialty("Pediatria-" + suffix);
		createDoctor("Inactivo", "Solo", "inactive-only." + suffix + "@demo.com", "DOC-NONE-" + suffix,
				"LIC-NONE-" + suffix, false, specialty);

		// when
		List<Doctor> activeBySpecialty = doctorRepository.findBySpecialtyIdAndActiveTrue(specialty.getId(), Pageable.unpaged())
				.getContent();

		// then
		assertThat(activeBySpecialty).isEmpty();
	}

	private Specialty createSpecialty(String name) {
		return specialtyRepository.save(Specialty.builder()
				.name(name)
				.description("Especialidad")
				.active(true)
				.createdAt(Instant.now())
				.build());
	}

	private Doctor createDoctor(String firstName, String lastName, String email, String documentNumber,
			String licenseNumber, boolean active, Specialty specialty) {
		return doctorRepository.save(Doctor.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.phone("3000000000")
				.documentType(DocumentType.CC)
				.documentNumber(documentNumber)
				.gender(Gender.MALE)
				.active(active)
				.createdAt(Instant.now())
				.licenseNumber(licenseNumber)
				.specialty(specialty)
				.build());
	}
}
