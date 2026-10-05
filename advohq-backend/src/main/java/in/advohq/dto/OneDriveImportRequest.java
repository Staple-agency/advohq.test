package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Import one OneDrive file into the Library. {@code itemId} is a Graph drive
 * item id (format-checked server-side); {@code caseId} is verified against the
 * caller's own cases before anything is downloaded.
 */
public record OneDriveImportRequest(
        @NotBlank @Size(max = 200) String itemId,
        UUID caseId
) {}
