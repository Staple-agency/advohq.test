package in.advohq.dto;

/**
 * The URL the browser navigates to (top-level) to begin Microsoft sign-in. It
 * carries a single-use, two-minute ticket — never the AdvoHQ JWT.
 */
public record OneDriveAuthorizeResponse(String loginUrl) {}
