package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A record of every invoice PDF the user has generated. The PDF bytes live in
 * S3 under {@link #s3Key}; this row holds the metadata shown in the Settings
 * history list and the JSON snapshot that lets the invoice generator re-open
 * a past invoice for edit.
 */
@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "invoice_no", nullable = false, length = 120)
    private String invoiceNo;

    @Column(name = "customer_name", length = 300)
    private String customerName;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    /** Total in the smallest INR unit (paise) so we never lose cents to float rounding. */
    @Column(name = "total_paise", nullable = false)
    private long totalPaise = 0;

    @Column(nullable = false, length = 40)
    private String status = "Unpaid";

    /** Full JSON snapshot of the invoice at issue time (customer, items, company). */
    @Column(columnDefinition = "text")
    private String snapshot;

    @Column(name = "s3_key", nullable = false, length = 600)
    private String s3Key;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
