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
 * A user's scan-to-email address. The token is the secret part of
 * {@code scan-<token>@<inbound domain>}; anyone who knows the address can
 * drop scans into this user's "Scans" folder, so it can be rotated.
 */
@Entity
@Table(name = "scan_inboxes")
@Getter
@Setter
@NoArgsConstructor
public class ScanInbox {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, unique = true, length = 40)
    private String token;

    /** The "Scans" folder; null until the first scan arrives or after it was deleted. */
    @Column(name = "case_id")
    private UUID caseId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
