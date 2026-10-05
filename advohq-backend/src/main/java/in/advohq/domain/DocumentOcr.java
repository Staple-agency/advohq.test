package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * OCR text for one document, read by Sarvam Document AI. One row per document:
 * OCR is billed per page, so a file is never read twice.
 */
@Entity
@Table(name = "document_ocr")
@Getter
@Setter
@NoArgsConstructor
public class DocumentOcr {

    public enum Status { QUEUED, RUNNING, DONE, FAILED }

    @Id
    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.QUEUED;

    @Column(nullable = false, length = 10)
    private String language = "en-IN";

    @Column(name = "pages_total", nullable = false)
    private int pagesTotal;

    @Column(name = "pages_done", nullable = false)
    private int pagesDone;

    /** The document was longer than the page cap; only the first pages were read. */
    @Column(nullable = false)
    private boolean truncated;

    @Column(name = "extracted_text", columnDefinition = "text")
    private String extractedText;

    /** User-safe message; never an exception dump. */
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
