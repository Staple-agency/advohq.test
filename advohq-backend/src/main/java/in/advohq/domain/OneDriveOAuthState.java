package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * One in-flight Microsoft OAuth round trip. Only hashes of the ticket, state
 * and browser nonce are stored; the PKCE verifier is encrypted. Rows are
 * single-use and short-lived, and are deleted once the callback completes.
 */
@Entity
@Table(name = "onedrive_oauth_states")
@Getter
@Setter
@NoArgsConstructor
public class OneDriveOAuthState {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "ticket_hash", nullable = false, unique = true, length = 64)
    private String ticketHash;

    @Column(name = "state_hash", unique = true, length = 64)
    private String stateHash;

    @Column(name = "nonce_hash", length = 64)
    private String nonceHash;

    @Column(name = "code_verifier_enc", columnDefinition = "text")
    private String codeVerifierEnc;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "ticket_used_at")
    private Instant ticketUsedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
