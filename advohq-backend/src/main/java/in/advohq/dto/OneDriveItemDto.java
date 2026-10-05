package in.advohq.dto;

import java.time.Instant;

/**
 * One file or folder in the user's OneDrive, as shown in the import picker.
 * {@code importable} is the server's verdict (type allowlist + size limit), so
 * the UI never has to duplicate those rules.
 */
public record OneDriveItemDto(
        String id,
        String name,
        boolean folder,
        Integer childCount,
        long size,
        String mimeType,
        Instant lastModified,
        boolean importable
) {}
