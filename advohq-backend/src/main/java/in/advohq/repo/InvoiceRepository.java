package in.advohq.repo;

import in.advohq.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    /** Newest invoice first — matches how the Settings history list displays them. */
    List<Invoice> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Invoice> findByIdAndUserId(UUID id, UUID userId);
}
