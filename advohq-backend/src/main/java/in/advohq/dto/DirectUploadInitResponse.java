package in.advohq.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Everything the browser needs to upload a large file straight to S3:
 * the upload's id (ours, not S3's), how big each part must be, how many parts
 * there are, and signed URLs for the first batch of them.
 */
public record DirectUploadInitResponse(
        UUID uploadId,
        long partSizeBytes,
        int partCount,
        List<DirectUploadPartUrl> urls,
        Instant expiresAt
) {}
