package in.advohq.repo;

import in.advohq.domain.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImportJobRepository extends JpaRepository<ImportJob, UUID> {

    /** Jobs are always looked up by id *and* owner. */
    Optional<ImportJob> findByIdAndUserId(UUID id, UUID userId);
}
