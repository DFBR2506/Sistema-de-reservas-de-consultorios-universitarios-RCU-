package co.edu.unimagdalena.RCU.domine.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import co.edu.unimagdalena.RCU.domine.entities.Appointment;
import co.edu.unimagdalena.RCU.domine.entities.AppointmentType;
import co.edu.unimagdalena.RCU.domine.entities.Doctor;
import co.edu.unimagdalena.RCU.domine.entities.Office;
import co.edu.unimagdalena.RCU.domine.entities.Patient;
import co.edu.unimagdalena.RCU.domine.entities.Specialty;
import co.edu.unimagdalena.RCU.domine.entities.enums.DocumentType;
import co.edu.unimagdalena.RCU.domine.entities.enums.Gender;
import co.edu.unimagdalena.RCU.domine.entities.enums.Status;

class AppointmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    AppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("Appointment: detecta traslape para doctor/office/patient")
    void shouldDetectOverlapForDoctorOfficeAndPatient() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));

	// when
	boolean doctorOverlap = appointmentRepository
		.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(data.doctor.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));
	boolean officeOverlap = appointmentRepository
		.existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(data.office.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));
	boolean patientOverlap = appointmentRepository
		.existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(data.patient.getId(),
			Instant.parse("2026-04-01T11:30:00Z"),
			Instant.parse("2026-04-01T10:30:00Z"));

	// then
	assertThat(doctorOverlap).isTrue();
	assertThat(officeOverlap).isTrue();
	assertThat(patientOverlap).isTrue();
    }

    @Test
    @DisplayName("Appointment: no detecta traslape cuando los rangos no coinciden")
    void shouldNotDetectOverlapWhenRangesDoNotIntersect() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));

	// when / then
	assertThat(appointmentRepository.existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan(
		data.doctor.getId(), Instant.parse("2026-04-01T13:00:00Z"), Instant.parse("2026-04-01T12:00:00Z"))).isFalse();
	assertThat(appointmentRepository.existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan(
		data.office.getId(), Instant.parse("2026-04-01T13:00:00Z"), Instant.parse("2026-04-01T12:00:00Z"))).isFalse();
	assertThat(appointmentRepository.existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan(
		data.patient.getId(), Instant.parse("2026-04-01T13:00:00Z"), Instant.parse("2026-04-01T12:00:00Z"))).isFalse();
    }

    @Test
    @DisplayName("Appointment: filtra por estado y rangos")
    void shouldFilterByStatusAndRanges() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.CANCELLED,
		Instant.parse("2026-04-01T12:00:00Z"), Instant.parse("2026-04-01T13:00:00Z"));

	// when / then
	assertThat(appointmentRepository.findByPatientIdAndStatus(data.patient.getId(), Status.SCHEDULED)).hasSize(1);
	assertThat(appointmentRepository.findByDoctorIdAndStartAtBetween(data.doctor.getId(),
		Instant.parse("2026-04-01T00:00:00Z"), Instant.parse("2026-04-01T23:59:59Z"))).hasSize(2);
	assertThat(appointmentRepository.findByOfficeIdAndStartAtBetween(data.office.getId(),
		Instant.parse("2026-04-01T00:00:00Z"), Instant.parse("2026-04-01T23:59:59Z"))).hasSize(2);
	assertThat(appointmentRepository.findByStartAtBetween(
		Instant.parse("2026-04-01T00:00:00Z"), Instant.parse("2026-04-01T23:59:59Z"))).hasSize(2);
    }

    @Test
    @DisplayName("Appointment: devuelve vacio cuando no hay coincidencias")
    void shouldReturnEmptyWhenNoFiltersMatch() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));

	// when / then
	assertThat(appointmentRepository.findByPatientIdAndStatus(data.patient.getId(), Status.NO_SHOW)).isEmpty();
	assertThat(appointmentRepository.findByDoctorIdAndStartAtBetween(data.doctor.getId(),
		Instant.parse("2026-04-02T00:00:00Z"), Instant.parse("2026-04-02T23:59:59Z"))).isEmpty();
	assertThat(appointmentRepository.findByStartAtBetween(
		Instant.parse("2026-04-02T00:00:00Z"), Instant.parse("2026-04-02T23:59:59Z"))).isEmpty();
    }

    @Test
    @DisplayName("Appointment: slots ocupados excluyen citas CANCELLED")
    void shouldExcludeCancelledAppointmentsFromBookedSlots() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.CANCELLED,
		Instant.parse("2026-04-01T12:00:00Z"), Instant.parse("2026-04-01T13:00:00Z"));

	// when
	List<Appointment> bookedSlots = appointmentRepository.findBookedSlotsByDoctorAndDate(
		data.doctor.getId(),
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-02T00:00:00Z"));

	// then
	assertThat(bookedSlots).hasSize(1);
	assertThat(bookedSlots.get(0).getStatus()).isEqualTo(Status.SCHEDULED);
    }

    @Test
    @DisplayName("Appointment: slots ocupados se ordenan por startAt asc")
    void shouldReturnBookedSlotsOrderedByStartAt() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);

	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T11:00:00Z"), Instant.parse("2026-04-01T11:30:00Z"));
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T09:00:00Z"), Instant.parse("2026-04-01T09:30:00Z"));

	// when
	List<Appointment> bookedSlots = appointmentRepository.findBookedSlotsByDoctorAndDate(
		data.doctor.getId(),
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-02T00:00:00Z"));

	// then
	assertThat(bookedSlots).hasSize(2);
	assertThat(bookedSlots.get(0).getStartAt()).isEqualTo(Instant.parse("2026-04-01T09:00:00Z"));
	assertThat(bookedSlots.get(1).getStartAt()).isEqualTo(Instant.parse("2026-04-01T11:00:00Z"));
    }

    @Test
    @DisplayName("Appointment: calcula ocupacion por rango y por dia")
    void shouldCalculateOfficeOccupancyReports() {
	// given
	ReportData reportData = createReportData();

	// when
	List<Object[]> officeOccupancy = appointmentRepository.findOfficeOccupancy(
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-30T23:59:59Z"),
		Pageable.unpaged()).getContent();

	// then
	assertThat(officeOccupancy).isNotEmpty();
	assertThat(officeOccupancy.get(0)[1]).isEqualTo(reportData.alpha.office.getCode());
	assertThat(((Long) officeOccupancy.get(0)[2])).isEqualTo(7L);

	// when
	List<Object[]> officeDailyOccupancy = appointmentRepository.findOfficeDailyOccupancy(
		Instant.parse("2026-04-02T00:00:00Z"),
		Instant.parse("2026-04-02T23:59:59Z"));

	// then
	assertThat(officeDailyOccupancy).isNotEmpty();
	assertThat(officeDailyOccupancy.get(0)[1]).isEqualTo(reportData.alpha.office.getCode());
	assertThat(((Long) officeDailyOccupancy.get(0)[2])).isEqualTo(1L);
    }

    @Test
    @DisplayName("Appointment: calcula ranking de productividad y no-show")
    void shouldCalculateProductivityAndNoShowRankings() {
	// given
	ReportData reportData = createReportData();

	// when
	List<Object[]> doctorProductivity = appointmentRepository.findDoctorProductivity(Pageable.unpaged()).getContent();

	// then
	assertThat(doctorProductivity).isNotEmpty();
	assertThat((String) doctorProductivity.get(0)[1]).isEqualTo(reportData.alpha.doctor.getFirstName());
	assertThat(((Long) doctorProductivity.get(0)[3])).isEqualTo(2L);

	// when
	List<Object[]> noShowPatients = appointmentRepository.findNoShowPatients(
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-30T23:59:59Z"),
		Pageable.unpaged()).getContent();

	// then
	assertThat(noShowPatients).isNotEmpty();
	assertThat((String) noShowPatients.get(0)[1]).isEqualTo(reportData.alpha.patient.getFirstName());
	assertThat(((Long) noShowPatients.get(0)[3])).isEqualTo(2L);
    }

    @Test
    @DisplayName("Appointment: ranking de productividad vacío cuando no hay COMPLETED")
    void shouldReturnEmptyProductivityWhenNoCompletedAppointments() {
	// given
	String suffix = UUID.randomUUID().toString().substring(0, 8);
	TestData data = createBaseData(suffix);
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.SCHEDULED,
		Instant.parse("2026-04-01T10:00:00Z"), Instant.parse("2026-04-01T11:00:00Z"));
	saveAppointment(data.doctor, data.patient, data.office, data.type, Status.NO_SHOW,
		Instant.parse("2026-04-01T12:00:00Z"), Instant.parse("2026-04-01T13:00:00Z"));

	// when
	List<Object[]> doctorProductivity = appointmentRepository.findDoctorProductivity(Pageable.unpaged()).getContent();

	// then
	assertThat(doctorProductivity).isEmpty();
    }

    @Test
    @DisplayName("Appointment: cuenta canceladas/no-show por especialidad y vacios fuera de rango")
    void shouldCountCancelledAndNoShowBySpecialtyAndReturnEmptyOutsideRange() {
	// given
	createReportData();

	// when
	List<Object[]> cancelledNoShowBySpecialty = appointmentRepository.countCancelledAndNoShowBySpecialty(
		Instant.parse("2026-04-01T00:00:00Z"),
		Instant.parse("2026-04-30T23:59:59Z"));

	// then
	assertThat(cancelledNoShowBySpecialty).isNotEmpty();
	Object[] alphaSpecialtyStats = cancelledNoShowBySpecialty.stream()
		.filter(row -> row[1].equals("Especialidad-alpha"))
		.findFirst()
		.orElseThrow();
	assertThat(((Long) alphaSpecialtyStats[2])).isEqualTo(1L);
	assertThat(((Long) alphaSpecialtyStats[3])).isEqualTo(2L);

	// when / then
	assertThat(appointmentRepository.findOfficeOccupancy(
		Instant.parse("2026-05-01T00:00:00Z"),
		Instant.parse("2026-05-31T23:59:59Z"),
		Pageable.unpaged()).getContent()).isEmpty();
	assertThat(appointmentRepository.findNoShowPatients(
		Instant.parse("2026-05-01T00:00:00Z"),
		Instant.parse("2026-05-31T23:59:59Z"),
		Pageable.unpaged()).getContent()).isEmpty();
	assertThat(appointmentRepository.countCancelledAndNoShowBySpecialty(
		Instant.parse("2026-05-01T00:00:00Z"),
		Instant.parse("2026-05-31T23:59:59Z"))).isEmpty();
    }

    private ReportData createReportData() {
	TestData alpha = createBaseData("alpha");
	TestData beta = createBaseData("beta");

	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.SCHEDULED,
		Instant.parse("2026-04-02T10:00:00Z"), Instant.parse("2026-04-02T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.SCHEDULED,
		Instant.parse("2026-04-03T10:00:00Z"), Instant.parse("2026-04-03T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.COMPLETED,
		Instant.parse("2026-04-04T10:00:00Z"), Instant.parse("2026-04-04T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.COMPLETED,
		Instant.parse("2026-04-05T10:00:00Z"), Instant.parse("2026-04-05T11:00:00Z"));
	saveAppointment(beta.doctor, beta.patient, beta.office, beta.type, Status.COMPLETED,
		Instant.parse("2026-04-06T10:00:00Z"), Instant.parse("2026-04-06T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.NO_SHOW,
		Instant.parse("2026-04-07T10:00:00Z"), Instant.parse("2026-04-07T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.NO_SHOW,
		Instant.parse("2026-04-08T10:00:00Z"), Instant.parse("2026-04-08T11:00:00Z"));
	saveAppointment(beta.doctor, beta.patient, beta.office, beta.type, Status.NO_SHOW,
		Instant.parse("2026-04-09T10:00:00Z"), Instant.parse("2026-04-09T11:00:00Z"));
	saveAppointment(alpha.doctor, alpha.patient, alpha.office, alpha.type, Status.CANCELLED,
		Instant.parse("2026-04-10T10:00:00Z"), Instant.parse("2026-04-10T11:00:00Z"));

	return new ReportData(alpha, beta);
    }

    private Appointment saveAppointment(Doctor doctor, Patient patient, Office office, AppointmentType type, Status status,
	    Instant start, Instant end) {
	return appointmentRepository.save(Appointment.builder()
		.doctor(doctor)
		.patient(patient)
		.office(office)
		.appointmentType(type)
		.status(status)
		.note("nota")
		.startAt(start)
		.endAt(end)
		.createdAt(Instant.now())
		.build());
    }

    private TestData createBaseData(String suffix) {
	Specialty specialty = specialtyRepository.save(Specialty.builder()
		.name("Especialidad-" + suffix)
		.description("desc")
		.active(true)
		.createdAt(Instant.now())
		.build());

	Doctor doctor = doctorRepository.save(Doctor.builder()
		.firstName("Doctor" + suffix)
		.lastName("Apellido")
		.email("doctor." + suffix + "@demo.com")
		.phone("3333333333")
		.documentType(DocumentType.CC)
		.documentNumber("DOCA-" + suffix)
		.gender(Gender.MALE)
		.active(true)
		.createdAt(Instant.now())
		.licenseNumber("LICA-" + suffix)
		.specialty(specialty)
		.build());

	Patient patient = patientRepository.save(Patient.builder()
		.firstName("Paciente" + suffix)
		.lastName("Apellido")
		.email("patient." + suffix + "@demo.com")
		.phone("3444444444")
		.documentType(DocumentType.CC)
		.documentNumber("PACA-" + suffix)
		.gender(Gender.OTHER)
		.active(true)
		.createdAt(Instant.now())
		.build());

	Office office = officeRepository.save(Office.builder()
		.code("OF-" + suffix)
		.floor(2)
		.active(true)
		.createdAt(Instant.now())
		.build());

	AppointmentType type = appointmentTypeRepository.save(AppointmentType.builder()
		.name("Tipo-" + suffix)
		.description("desc")
		.active(true)
		.durationMinutes(30)
		.createdAt(Instant.now())
		.build());

	return new TestData(doctor, patient, office, type);
    }

    private record TestData(Doctor doctor, Patient patient, Office office, AppointmentType type) {
    }

	private record ReportData(TestData alpha, TestData beta) {
	}
}
