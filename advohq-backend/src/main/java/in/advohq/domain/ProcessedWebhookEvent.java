package in.advohq.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * One row per Razorpay webhook event we have already applied.
 *
 * <p>Razorpay retries a webhook until it receives a 2xx, and can redeliver an
 * event even after a successful response. Without this record a redelivered
 * {@code subscription.charged} would extend the paid period a second time for a
 * single payment. The event id is the primary key, so a replay collides on
 * insert and is skipped.
 */
@Entity
@Table(name = "processed_webhook_events")
@Getter
@Setter
@NoArgsConstructor
public class ProcessedWebhookEvent {

    /** Razorpay's {@code x-razorpay-event-id} header value. */
    @Id
    @Column(name = "event_id", nullable = false, length = 160)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 80)
    private String eventType;

    @CreationTimestamp
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt;
}
