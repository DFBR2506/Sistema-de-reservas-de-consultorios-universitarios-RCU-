package co.edu.unimagdalena.RCU.security.domine.repositories;

import co.edu.unimagdalena.RCU.security.domine.entities.AppUser;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByDocumentNumberIgnoreCase(String documentNumber);
    boolean existsByDocumentNumberIgnoreCase(String documentNumber);
}
