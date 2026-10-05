package in.advohq.repo;

import in.advohq.domain.DocumentOcr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentOcrRepository extends JpaRepository<DocumentOcr, UUID> {

    Optional<DocumentOcr> findByDocumentIdAndUserId(UUID documentId, UUID userId);
}
