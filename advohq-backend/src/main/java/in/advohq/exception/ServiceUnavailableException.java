package in.advohq.exception;

/** An optional integration isn't configured in this environment → 503. */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
