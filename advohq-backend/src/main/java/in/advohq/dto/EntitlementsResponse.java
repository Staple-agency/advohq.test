package in.advohq.dto;

import java.time.Instant;

/**
 * What the caller may do right now. The frontend uses it to show/hide features;
 * the frontend server's AI proxy uses it to <em>enforce</em> them.
 *
 * @param tier            BASIC | PROFESSIONAL | PREMIUM (paid), TRIAL, or NONE
 * @param tierName        display name, e.g. "Professional" or "Free trial"
 * @param paidUntil       end of the paid period (paid tiers only)
 * @param trialEndsAt     end of the free trial, if the account has one
 * @param storageUsedBytes   bytes of all documents, including Trash
 * @param storageLimitBytes  0 when uploads aren't allowed at all
 */
public record EntitlementsResponse(
        String tier,
        String tierName,
        Instant paidUntil,
        Instant trialEndsAt,
        long storageUsedBytes,
        long storageLimitBytes,
        boolean canUpload,
        boolean aiPresets,
        boolean aiFreeform,
        boolean docIntel,
        boolean sarvam
) {}
