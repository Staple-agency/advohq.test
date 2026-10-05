package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Start a large upload. The server picks the S3 key; the browser never does. */
public record DirectUploadInitRequest(
        @NotBlank @Size(max = 300) String fileName,
        @Size(max = 160) String contentType,
        @Positive long sizeBytes,
        UUID caseId
) {}
