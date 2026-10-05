package in.advohq.dto;

/**
 * Whether OneDrive import is available in this deploy and linked for the
 * caller. Never carries tokens — only the linked account's display details.
 */
public record OneDriveStatusResponse(
        boolean available,
        boolean connected,
        String accountName,
        String accountEmail
) {}
