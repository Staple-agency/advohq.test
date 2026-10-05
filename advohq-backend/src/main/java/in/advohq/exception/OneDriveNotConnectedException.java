package in.advohq.exception;

/**
 * The user has no usable OneDrive link — never connected, disconnected, or
 * Microsoft revoked the refresh token. Mapped to 409 (not 401/403): the
 * frontend treats 401/403 as "AdvoHQ session expired" and logs the user out,
 * whereas this only means "reconnect OneDrive".
 */
public class OneDriveNotConnectedException extends RuntimeException {
    public static final String CODE = "ONEDRIVE_NOT_CONNECTED";

    public OneDriveNotConnectedException(String message) {
        super(message);
    }
}
