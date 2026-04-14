# Sistema de Reservas de Consultorios Universitarios (RCU)

## Descripción general

API REST desarrollada con Java 21 y Spring Boot 4 para gestionar la agenda médica universitaria. Permite administrar pacientes, doctores, especialidades, consultorios, tipos de cita, horarios de atención y citas médicas, garantizando reglas de negocio reales, control de disponibilidad y reportes operativos.

---

## Stack tecnológico

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.5 | Framework backend |
| PostgreSQL | Latest | Base de datos relacional |
| MapStruct | 1.6.3 | Mapeo entre entidades y DTOs |
| Lombok | Latest | Reducción de boilerplate |
| JUnit 5 | Latest | Testing unitario e integración |
| Mockito | Latest | Mocking en tests de servicio |
| Testcontainers | Latest | Tests de integración con PostgreSQL real |

---

## Arquitectura

El proyecto aplica **arquitectura por capas (N-Layer Architecture)**:

```
Controller  →  Service  →  Repository  →  Base de datos
               ↑
             Mapper
               ↑
              DTO
```

### Decisión: ¿Por qué arquitectura por capas?

Se eligió arquitectura por capas porque:
- Separa claramente las responsabilidades de cada componente.
- Facilita el testing independiente de cada capa.
- Permite que dos desarrolladores trabajen en paralelo (uno en repository, otro en services).
- Cumple con los requerimientos académicos del proyecto.

---

## Estructura de paquetes

```
co.edu.unimagdalena.RCU
├── entities/
│   ├── enums/
│   │   ├── AppointmentStatus.java
│   │   ├── DocumentType.java
│   │   ├── DayOfWeek.java
│   │   ├── Gender.java
│   │   └── OfficeStatus.java
│   ├── Person.java          ← clase base abstracta
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Specialty.java
│   ├── DoctorSchedule.java
│   ├── AppointmentType.java
│   ├── Office.java
│   └── Appointment.java
├── dto/
│   ├── PatientDtos.java
│   ├── SpecialtyDtos.java
│   ├── DoctorDtos.java
│   ├── OfficeDtos.java
│   ├── AppointmentTypeDtos.java
│   ├── DoctorScheduleDtos.java
│   ├── AppointmentDtos.java
│   ├── AvailabilityDtos.java
│   └── ReportDtos.java
├── mapper/
│   ├── PatientMapper.java
│   ├── SpecialtyMapper.java
│   ├── DoctorMapper.java
│   ├── OfficeMapper.java
│   ├── AppointmentTypeMapper.java
│   ├── DoctorScheduleMapper.java
│   └── AppointmentMapper.java
├── repository/
│   ├── PatientRepository.java
│   ├── SpecialtyRepository.java
│   ├── DoctorRepository.java
│   ├── OfficeRepository.java
│   ├── AppointmentTypeRepository.java
│   ├── DoctorScheduleRepository.java
│   └── AppointmentRepository.java
├── service/
│   ├── PatientService.java
│   ├── SpecialtyService.java
│   ├── DoctorService.java
│   ├── OfficeService.java
│   ├── AppointmentTypeService.java
│   ├── DoctorScheduleService.java
│   ├── AppointmentService.java
│   ├── AvailabilityService.java
│   ├── ReportService.java
│   └── implementation/
│       ├── PatientServiceImpl.java
│       ├── SpecialtyServiceImpl.java
│       ├── DoctorServiceImpl.java
│       ├── OfficeServiceImpl.java
│       ├── AppointmentTypeServiceImpl.java
│       ├── DoctorScheduleServiceImpl.java
│       ├── AppointmentServiceImpl.java
│       ├── AvailabilityServiceImpl.java
│       └── ReportServiceImpl.java
├── controller/
│   └── ...
└── exceptions/
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    ├── ConflictException.java
    ├── ValidationException.java
    └── GlobalExceptionHandler.java
```

---

## Modelo de datos y entidades

### Decisión: Herencia con TABLE_PER_CLASS

Se utilizó `InheritanceType.TABLE_PER_CLASS` para la jerarquía `Person → Patient / Doctor` porque:
- El modelo requiere tablas independientes para `patients` y `doctors` sin una tabla `persons` intermedia.
- Evita columnas `NULL` innecesarias que generaría `SINGLE_TABLE`.
- No requiere `JOIN` en cada consulta como lo haría `JOINED`.
- `Patient` y `Doctor` tienen datos de contacto idénticos pero roles de negocio completamente distintos.

**Consecuencia técnica:** Se usa `GenerationType.UUID` en vez de `IDENTITY` porque con `TABLE_PER_CLASS` los IDs deben ser únicos globalmente entre todas las tablas hijas.

`Person` se declaró como clase `abstract` porque nunca se instancia directamente — siempre se trabaja con `Patient` o `Doctor`.

### Decisión: UUID como identificador

Se eligió `UUID` en vez de `Long` como tipo de ID porque:
- Evita exponer secuencias predecibles en la API.
- Facilita la generación de IDs distribuida sin depender de la base de datos.
- Es consistente con el estilo del proyecto del curso.

### Decisión: @SuperBuilder en lugar de @Builder

Con herencia, `@Builder` de Lombok solo incluye los campos de la clase hija, ignorando los del padre. Se usó `@SuperBuilder` para que el builder de `Doctor` y `Patient` incluya automáticamente los campos de `Person`.

### Tablas generadas

| Tabla | Descripción |
|---|---|
| `patients` | Datos completos del paciente (heredados de Person) |
| `doctors` | Datos completos del doctor + license_number + specialty_id |
| `specialties` | Catálogo de especialidades médicas |
| `offices` | Consultorios físicos con código y piso |
| `appointment_types` | Tipos de cita con duración en minutos |
| `doctor_schedules` | Franjas horarias de atención por día de la semana |
| `appointments` | Reservas médicas con estado y trazabilidad |

### Trazabilidad

Todas las entidades incluyen `createdAt` y `updatedAt` de tipo `Instant` para registrar cuándo fue creado y modificado cada registro. Estos campos son seteados por el service, no por el cliente.

---

## DTOs

### Decisión: Records de Java agrupados por entidad

Se implementaron los DTOs como `records` de Java agrupados en una sola clase por entidad (ej: `PatientDtos.java`) porque:
- Los `records` son inmutables por naturaleza, ideal para objetos de transferencia.
- Reducen significativamente el boilerplate comparado con clases tradicionales.
- Agrupar por entidad facilita la navegación y mantiene cohesión.
- Implementan `Serializable` para compatibilidad con serialización.

### Separación request/response

**Request:**
- `CreateRequest`: no incluye `id` (lo genera la BD), ni `active` (siempre inicia en `true`), ni campos calculados por el sistema (`endAt`, `status`, `createdAt`).
- `UpdateRequest`: no incluye `id` (va en la URL), pero sí incluye `active` para poder activar/desactivar entidades.

**Response:**
- Siempre incluye `id` para que el cliente pueda referenciar el recurso en operaciones posteriores.
- En relaciones, se expone solo el `id` de la entidad relacionada (ej: `specialtyId` en `DoctorResponse`) para evitar respuestas anidadas innecesarias.

### DTOs sin entidad propia

`AvailabilityDtos` y `ReportDtos` no mapean ninguna entidad directamente. Son DTOs de solo lectura construidos por los services a partir de múltiples entidades:
- `AvailabilitySlotResponse`: construido por `AvailabilityServiceImpl` calculando slots libres.
- `OfficeOccupancyResponse`, `DoctorProductivityResponse`, `NoShowPatientResponse`: construidos por `ReportServiceImpl` a partir de queries JPQL agregadas.

---

## Mappers

### Decisión: MapStruct

Se usó MapStruct porque genera código en tiempo de compilación sin overhead en runtime. En los tests de servicio se usó `@Spy` con el mapper real (en vez de `@Mock`) para validar que el mapeo real funciona correctamente junto con la lógica de negocio.

### Campos ignorados en toEntity

| Campo | Razón |
|---|---|
| `id` | Generado por la BD con `GenerationType.UUID` |
| `active` | Seteado explícitamente en el service como `true` |
| `createdAt` / `updatedAt` | Gestionados por el service con `Instant.now()` |
| Relaciones (`@ManyToOne`) | Resueltas por el service buscando entidades por ID |
| Colecciones (`@OneToMany`) | No se mapean en creación |

### Decisión: @BeanMapping en updateEntity

Se aplicó `@BeanMapping(nullValuePropertyMappingStrategy = IGNORE)` en todos los `updateEntity` para que un campo `null` en el request no sobreescriba el valor existente en la base de datos, permitiendo updates parciales.

**Importante:** `active` NO se ignora en `updateEntity` para que el cliente pueda activar o desactivar entidades mediante el endpoint de actualización.

### Mapeo de relaciones en toResponse

Para entidades con relaciones `@ManyToOne`, se usa `@Mapping(source = "entity.id", target = "entityId")` para extraer solo el ID de la entidad relacionada en el response:

```java
@Mapping(source = "specialty.id", target = "specialtyId")
DoctorResponse toResponse(Doctor doctor);

@Mapping(source = "doctor.id", target = "doctorId")
@Mapping(source = "patient.id", target = "patientId")
AppointmentResponse toResponse(Appointment appointment);
```

---

## Capa de servicios

### Decisión: Interfaz + Implementación

Se separó cada service en interfaz e implementación porque:
- Permite cambiar la implementación sin afectar el resto del código.
- Facilita el mocking en tests con Mockito.
- Es buena práctica en arquitectura por capas.

### Orden de implementación por dependencias

```
1. SpecialtyService       → sin dependencias
2. PatientService         → sin dependencias
3. OfficeService          → sin dependencias
4. AppointmentTypeService → sin dependencias
5. DoctorService          → depende de Specialty
6. DoctorScheduleService  → depende de Doctor
7. AvailabilityService    → depende de Doctor + DoctorSchedule + Appointment
8. AppointmentService     → depende de todo lo anterior
9. ReportService          → depende de Appointment
```

### Decisión: @Transactional

Se aplicó `@Transactional` a nivel de clase en los services y `@Transactional(readOnly = true)` en los métodos de solo lectura para optimizar el acceso a la base de datos y garantizar consistencia en operaciones de escritura.

### Validaciones en el service

Cada service aplica validaciones en dos niveles:

**Validaciones de nulidad y formato:**
```java
private static void requireNonNull(Object obj, String message) {
    if (Objects.isNull(obj)) throw new IllegalArgumentException(message);
}
private static void requireNonBlank(String str, String message) {
    if (str == null || str.isBlank()) throw new IllegalArgumentException(message);
}
```

**Validaciones de negocio:** usan las excepciones personalizadas del paquete `exceptions`.

---

## Reglas de negocio implementadas

### Creación de citas
- No se puede crear una cita en el pasado.
- El paciente, doctor y consultorio deben existir y estar activos.
- El tipo de cita debe estar activo.
- `endAt` es calculado por el sistema: `startAt + durationMinutes * 60`. El cliente nunca envía `endAt`.
- La cita debe caer dentro del horario laboral del doctor en ese día de la semana.
- No puede haber traslape de horario para el doctor, el consultorio ni el paciente.
- Toda cita nueva inicia con estado `SCHEDULED`.

### Transiciones de estado

```
SCHEDULED → CONFIRMED  (confirm)
SCHEDULED → CANCELLED  (cancel, requiere motivo obligatorio)
CONFIRMED → CANCELLED  (cancel, requiere motivo obligatorio)
CONFIRMED → COMPLETED  (complete, solo si ya pasó la hora de inicio)
CONFIRMED → NO_SHOW    (markAsNoShow, solo si ya pasó la hora de inicio)
```

### Validaciones de unicidad
- Email y número de documento únicos por persona (paciente y doctor).
- Número de licencia único por doctor.
- Nombre único por especialidad y tipo de cita.
- Código único por consultorio.
- Un doctor no puede tener dos horarios para el mismo día de la semana.

### Servicio de disponibilidad

`AvailabilityService` calcula slots disponibles en una fecha para un doctor y consultorio:
1. Obtiene el horario del doctor para ese día de la semana.
2. Convierte el horario a `Instant` en UTC.
3. Obtiene las citas existentes del doctor y del consultorio en ese rango.
4. Divide la jornada en bloques de 30 minutos.
5. Devuelve solo los bloques libres tanto para el doctor como para el consultorio.

---

## Manejo de excepciones

| Excepción | HTTP | Cuándo se lanza |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entidad no encontrada por ID |
| `BusinessException` | 400 | Regla de negocio violada |
| `ConflictException` | 409 | Traslape de horario o duplicado |
| `ValidationException` | 400 | Validación de entrada fallida |

El `GlobalExceptionHandler` con `@RestControllerAdvice` captura todas las excepciones y devuelve respuestas JSON estandarizadas con `timestamp`, `status` y `message`.

---

## Capa de repositorio

### Descripción general

La capa de repositorio encapsula el acceso a datos para las entidades clínicas principales usando Spring Data JPA con dos enfoques:
- Query methods derivados por convención de nombre.
- Consultas JPQL para agregación, ranking y reportes.

La suite de tests valida estos métodos sobre PostgreSQL real mediante Testcontainers, reduciendo riesgo de diferencias entre H2/in-memory y el comportamiento real en SQL.

### Responsabilidades

- Exponer operaciones de lectura y validación de negocio orientadas a persistencia.
- Resolver consultas de disponibilidad, traslapes y reportes agregados.
- Mantener contratos simples para que la capa de servicio no dependa de SQL embebido.
- Proveer pruebas de integración que validen consultas reales contra esquema JPA.

### AppointmentRepository

Centraliza consultas de citas para detección de traslapes, filtros por estado y construcción de reportes.

| Método | Descripción | Retorno |
|---|---|---|
| `existsByDoctorIdAndStartAtLessThanAndEndAtGreaterThan` | Valida traslape de citas por doctor | boolean |
| `existsByOfficeIdAndStartAtLessThanAndEndAtGreaterThan` | Valida traslape de citas por consultorio | boolean |
| `existsByPatientIdAndStartAtLessThanAndEndAtGreaterThan` | Valida traslape de citas por paciente | boolean |
| `findByPatientIdAndStatus` | Citas de un paciente filtradas por estado | List\<Appointment\> |
| `findByDoctorIdAndStartAtBetween` | Citas de un doctor en rango temporal | List\<Appointment\> |
| `findByOfficeIdAndStartAtBetween` | Citas de un consultorio en rango temporal | List\<Appointment\> |
| `findByStartAtBetween` | Citas globales por rango de fecha | List\<Appointment\> |
| `findBookedSlotsByDoctorAndDate` | Bloques ocupados de un doctor excluyendo CANCELLED | List\<Appointment\> |
| `findOfficeOccupancy` | Ocupación por consultorio en rango temporal | List\<Object[]\> |
| `findOfficeDailyOccupancy` | Ocupación diaria por consultorio | List\<Object[]\> |
| `countCancelledAndNoShowBySpecialty` | Agrega CANCELLED y NO_SHOW por especialidad | List\<Object[]\> |
| `findDoctorProductivity` | Ranking de doctores por citas COMPLETED | List\<Object[]\> |
| `findNoShowPatients` | Ranking de pacientes con mayor NO_SHOW | List\<Object[]\> |

**Lógica de traslape:** `existing.start < requestedEnd AND existing.end > requestedStart`. Los bloques ocupados excluyen CANCELLED para no bloquear disponibilidad.

### DoctorRepository

| Método | Descripción | Retorno |
|---|---|---|
| `findBySpecialtyIdAndActiveTrue` | Doctores activos de una especialidad | List\<Doctor\> |
| `existsByEmail` | Verifica unicidad de email | boolean |
| `existsByDocumentNumber` | Verifica unicidad de número de documento | boolean |
| `existsByLicenseNumber` | Verifica unicidad de licencia médica | boolean |

### DoctorScheduleRepository

| Método | Descripción | Retorno |
|---|---|---|
| `existsByDoctorIdAndDayOfWeek` | Confirma existencia de horario para doctor + día | boolean |
| `findByDoctorId` | Lista todos los horarios de un doctor | List\<DoctorSchedule\> |
| `findByDoctorIdAndDayOfWeek` | Horario puntual por doctor + día | Optional\<DoctorSchedule\> |

### OfficeRepository

| Método | Descripción | Retorno |
|---|---|---|
| `existsByCode` | Valida si ya existe un consultorio con el código | boolean |

### PatientRepository

| Método | Descripción | Retorno |
|---|---|---|
| `existsByEmail` | Valida existencia por email | boolean |
| `existsByDocumentNumber` | Valida existencia por número de documento | boolean |

### SpecialtyRepository

| Método | Descripción | Retorno |
|---|---|---|
| `existsByName` | Valida si existe una especialidad con ese nombre | boolean |

### AppointmentTypeRepository

| Método | Descripción | Retorno |
|---|---|---|
| `existsByName` | Valida si existe un tipo de cita con ese nombre | boolean |

---

## Pruebas

### Tests de mappers

Validan que MapStruct mapee correctamente entre entidades y DTOs:
- `toEntity`: campos mapeados correctamente, campos ignorados en `null`.
- `toResponse`: todos los campos del response presentes y con valores correctos.
- `updateEntity`: campos `null` en el request no sobreescriben valores existentes.

### Tests de services (Mockito)

Usan `@Spy` con el mapper real y `@Mock` para repositorios. Siguen la estructura Given/When/Then.

**Decisión: @Spy vs @Mock para mappers**

Se eligió `@Spy` en vez de `@Mock` para los mappers porque los tests de mapper ya validan el mapeo de forma aislada. En los tests de service, usar el mapper real garantiza que la integración service-mapper funciona correctamente sin necesidad de stubbear cada llamada al mapper.

| Test | Cobertura |
|---|---|
| `AppointmentServiceImplTest` | Creación con todas las validaciones, transiciones de estado, traslapes |
| `AvailabilityServiceImplTest` | Slots disponibles, jornada completa, doctor sin horario |
| `DoctorScheduleServiceImplTest` | Creación, doctor inactivo, horario duplicado, rango inválido |
| `SpecialtyServiceImplTest` | CRUD básico, nombre duplicado |
| `AppointmentTypeServiceImplTest` | Creación, duración inválida, nombre duplicado |
| `ReportServiceImplTest` | Ocupación, productividad, no-shows, rangos de fecha |

### Tests de repositorio (Testcontainers)

La clase base `AbstractRepositoryIT` configura `@DataJpaTest` con perfil `test` y PostgreSQL real via Testcontainers, evitando duplicar bootstrapping en cada test.

| Test | Cobertura |
|---|---|
| `AppointmentRepositoryTest` | Traslapes, filtros por estado, reportes de ocupación, productividad, no-show |
| `DoctorRepositoryTest` | Unicidad de email/documento/licencia, filtro por especialidad activa |
| `DoctorScheduleRepositoryTest` | Existencia por día, múltiples horarios, caso vacío |
| `PatientRepositoryTest` | Existencia por email y documento |
| `OfficeRepositoryTest` | Existencia por código |
| `SpecialtyRepositoryTest` | Existencia por nombre |
| `AppointmentTypeRepositoryTest` | Existencia por nombre |

### Notas técnicas sobre repositorios

- Los retornos `List<Object[]>` en reportes agregados funcionan correctamente. Para mayor mantenibilidad se recomienda migrar a proyecciones tipadas en el futuro.
- Los repositories no declaran checked exceptions; los errores de persistencia emergen como `DataAccessException`.
- La validación de argumentos nulos o de reglas de negocio permanece en los services, no en los repositories.

---

## Cómo ejecutar el proyecto

### Requisitos previos
- Java 21
- Docker (para Testcontainers)
- PostgreSQL configurado

### Configurar base de datos

En `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rcu_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### Compilar y ejecutar

```bash
# Compilar
./mvnw clean install -DskipTests

# Ejecutar
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test
```

---

## Autores

Proyecto académico — Programación Web
Universidad del Magdalena
Repositorio: [Sistema de Reservas de Consultorios Universitarios RCU](https://github.com/DFBR2506/Sistema-de-reservas-de-consultorios-universitarios-RCU-)
