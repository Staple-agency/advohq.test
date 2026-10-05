package in.advohq.dto;

import java.util.List;

/**
 * A page of a OneDrive folder. {@code nextPageToken} is an opaque Graph
 * skip-token; the client sends it back verbatim and the server rebuilds the
 * Graph URL itself — client-supplied URLs are never fetched.
 */
public record OneDriveListResponse(
        String folderId,
        String folderName,
        String parentId,
        List<OneDriveItemDto> items,
        String nextPageToken
) {}
