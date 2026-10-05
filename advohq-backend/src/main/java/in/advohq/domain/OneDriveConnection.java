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
 * A user's linked Microsoft account, used only to import files from OneDrive.
 *
 * <p>The token columns hold AES-GCM ciphertext produced by
 * {@link in.advohq.service.TokenCipher} — never plaintext tokens. They are
 * never exposed through any DTO.
 */
@Entity
@Table(name = "onedrive_connections")
@Getter
@Setter
@NoArgsConstructor
public class OneDriveConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    /** Microsoft Graph {@code /me} id of the linked account. */
    @Column(name = "ms_account_id", nullable = false, length = 120)
    private String msAccountId;

    /** Id of the linked account's OneDrive; imports must come from this drive. */
    @Column(name = "ms_drive_id", length = 200)
    private String msDriveId;

    @Column(name = "ms_display_name", length = 200)
    private String msDisplayName;

    @Column(name = "ms_email", length = 320)
    private String msEmail;

    @Column(name = "access_token_enc", nullable = false, columnDefinition = "text")
    private String accessTokenEnc;

    @Column(name = "refresh_token_enc", columnDefinition = "text")
    private String refreshTokenEnc;

    @Column(name = "access_token_expires_at", nullable = false)
    private Instant accessTokenExpiresAt;

    @Column(length = 500)
    private String scopes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
