package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * A browser-to-S3 multipart upload in progress. Deleted when the upload
 * completes, is abandoned, or expires (see {@code DirectUploadService}).
 */
@Entity
@Table(name = "pending_uploads")
@Getter
@Setter
@NoArgsConstructor
public class PendingUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "case_id")
    private UUID caseId;

    @Column(name = "file_name", nullable = false, length = 300)
    private String fileName;

    @Column(name = "content_type", length = 160)
    private String contentType;

    /** What the browser said the file weighs; the real size is re-read from S3 at the end. */
    @Column(name = "declared_size", nullable = false)
    private long declaredSize;

    @Column(name = "s3_key", nullable = false, unique = true, length = 600)
    private String s3Key;

    @Column(name = "upload_id", nullable = false, length = 400)
    private String uploadId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
