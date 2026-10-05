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
 * Local mirror of a Razorpay subscription. Razorpay owns the mandate and the
 * recurring charge schedule; this row is kept in sync by the webhook handler so
 * the app never has to call Razorpay to answer "is this user paid?".
 *
 * <p>Access itself is gated on {@code users.plan_expires_at} (see
 * {@link User#getPlanExpiresAt()}), which this table's webhook updates.
 */
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    /** Lifecycle, mapped from Razorpay's own subscription states. */
    public enum Status {
        /** Created locally + at Razorpay, but the user hasn't authorised the mandate yet. */
        CREATED,
        /** Mandate authorised; first charge may still be settling. */
        AUTHENTICATED,
        /** Paid and in good standing. */
        ACTIVE,
        /** Razorpay gave up retrying a failed charge — treat as unpaid once the period ends. */
        HALTED,
        /** Cancelled by the user or by us. */
        CANCELLED,
        /** Ran to the end of total_count cycles. */
        COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Our plan identifier (PROFESSIONAL | FIRM), not Razorpay's plan id. */
    @Column(name = "plan_code", nullable = false, length = 40)
    private String planCode;

    /** Price in paise — integer arithmetic only, same convention as {@link Invoice}. */
    @Column(name = "amount_paise", nullable = false)
    private long amountPaise;

    @Column(name = "razorpay_plan_id", nullable = false, length = 80)
    private String razorpayPlanId;

    @Column(name = "razorpay_subscription_id", nullable = false, unique = true, length = 80)
    private String razorpaySubscriptionId;

    @Column(name = "razorpay_payment_id", length = 80)
    private String razorpayPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Status status = Status.CREATED;

    /** End of the paid period, from Razorpay's {@code current_end}. */
    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
