package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(length = 20)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;

    /** Bumped on password change; JWTs carry it so old tokens die with the old password. */
    @Column(name = "token_version", nullable = false)
    private int tokenVersion = 0;

    /**
     * Denormalised billing state, maintained by the Razorpay webhook. Feature
     * gating reads these two columns instead of joining subscriptions or calling
     * Razorpay — a user is paid iff {@code planExpiresAt} is in the future.
     * Null on both means "never subscribed".
     */
    @Column(name = "plan_code", length = 40)
    private String planCode;

    @Column(name = "plan_expires_at")
    private Instant planExpiresAt;

    /** End of the free trial (Premium-level access). Set at registration; see V13. */
    @Column(name = "trial_ends_at")
    private Instant trialEndsAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
