package in.advohq.repo;

import in.advohq.domain.PendingUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PendingUploadRepository extends JpaRepository<PendingUpload, UUID> {

    /** Always look uploads up by id *and* owner — never by id alone. */
    Optional<PendingUpload> findByIdAndUserId(UUID id, UUID userId);

    List<PendingUpload> findTop50ByExpiresAtBefore(Instant cutoff);
}
