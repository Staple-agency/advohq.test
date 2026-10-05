package in.advohq.dto;

import java.time.Instant;

/**
 * The caller's current billing state. {@code active} is the single flag the UI
 * should branch on; it already accounts for the paid period not having lapsed.
 */
public record SubscriptionStatusResponse(boolean active,
                                         String planCode,
                                         String planName,
                                         String status,
                                         Instant currentPeriodEnd) {

    public static SubscriptionStatusResponse none() {
        return new SubscriptionStatusResponse(false, null, null, "NONE", null);
    }
}
