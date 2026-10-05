package in.advohq.dto;

/**
 * A user's scan-to-email address.
 *
 * @param address the address to save on the scanner, e.g. {@code scan-ab12cd34ef56@in.advohq.in}
 * @param enabled false while scan-to-email isn't set up on the server (the address won't receive mail yet)
 */
public record ScanInboxResponse(String address, boolean enabled) {}
