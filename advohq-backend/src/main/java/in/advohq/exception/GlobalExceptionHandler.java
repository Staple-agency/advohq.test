package in.advohq.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Central error translation. Extends {@link ResponseEntityExceptionHandler} so
 * that Spring's own MVC exceptions (unreadable body → 400, unsupported method →
 * 405, media type → 415, …) keep their correct HTTP status and are rendered
 * without leaking internals — while our domain exceptions and a final catch-all
 * are handled below. Nothing here ever echoes a raw exception message or stack
 * trace to the client; the full detail stays in the server logs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", message);
        return m;
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /** 402: the plan doesn't allow it. {@code code} lets the UI link to the subscription page. */
    @ExceptionHandler(PlanLimitException.class)
    public ResponseEntity<Map<String, Object>> handlePlanLimit(PlanLimitException ex) {
        Map<String, Object> b = body(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
        b.put("code", ex.code());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(b);
    }

    /** 409 with a machine-readable code so the UI can offer "Connect OneDrive". */
    @ExceptionHandler(OneDriveNotConnectedException.class)
    public ResponseEntity<Map<String, Object>> handleOneDriveNotConnected(OneDriveNotConnectedException ex) {
        Map<String, Object> b = body(HttpStatus.CONFLICT, ex.getMessage());
        b.put("code", OneDriveNotConnectedException.CODE);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(b);
    }

    /**
     * Microsoft Graph/identity failures. Their messages are written by
     * MicrosoftGraphClient to be user-safe (no tokens, no raw Graph bodies).
     * 502: the upstream failed, not the caller.
     */
    @ExceptionHandler(in.advohq.service.MicrosoftGraphClient.GraphException.class)
    public ResponseEntity<Map<String, Object>> handleGraph(in.advohq.service.MicrosoftGraphClient.GraphException ex) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        if (ex instanceof in.advohq.service.MicrosoftGraphClient.GraphNotFoundException) status = HttpStatus.NOT_FOUND;
        else if (ex instanceof in.advohq.service.MicrosoftGraphClient.FileTooLargeException) status = HttpStatus.PAYLOAD_TOO_LARGE;
        // A Graph 403 is about the OneDrive item, not the AdvoHQ session — 404
        // keeps the frontend from reading it as "logged out".
        else if (ex instanceof in.advohq.service.MicrosoftGraphClient.GraphForbiddenException) status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(body(status, ex.getMessage()));
    }

    /** A required integration isn't configured in this deploy (e.g. OneDrive env vars missing). */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleUnavailable(ServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(body(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(body(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
    }

    /**
     * Upload exceeded the configured multipart limit. As of Spring 6.1,
     * {@link ResponseEntityExceptionHandler} already declares an
     * {@code @ExceptionHandler} for {@link MaxUploadSizeExceededException} on its
     * built-in {@code handleException} method, so adding our own
     * {@code @ExceptionHandler} for the same type made the mapping ambiguous and
     * broke DispatcherServlet init on startup. We override the framework's
     * protected hook instead — a single mapping — and return our own 413 body.
     */
    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        return new ResponseEntity<>(body(HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds the maximum allowed size"),
                HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Bean-validation failures. Overrides the framework default so we can return
     * a per-field {@code fields} map (the frontend highlights the offending
     * inputs from it). Field names and validation messages are safe to expose;
     * they come from our own DTO constraints, not from internals.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> b = body(HttpStatus.BAD_REQUEST, "Validation failed");
        b.put("fields", ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                        (a, b2) -> a)));
        // Parent contract is ResponseEntity<Object>; build it directly so the
        // Map body is carried as Object rather than ResponseEntity<Map<...>>.
        return new ResponseEntity<>(b, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * A DB constraint tripped — e.g. two concurrent registrations racing on the
     * same username past the pre-check. Return a clean 409 instead of a 500 that
     * leaks the SQL error.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(body(HttpStatus.CONFLICT, "That change conflicts with existing data. Please try again."));
    }

    /**
     * Catch-all safety net for anything not handled above or by the framework.
     * Without it, an unexpected exception would fall through to the default
     * error controller and could echo its raw message to the client. We instead
     * log the full detail server-side and return a generic, opaque 500. The
     * short correlation id ties the client-visible response to the exact server
     * log line so real incidents stay debuggable.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        String errorId = UUID.randomUUID().toString().substring(0, 8);
        log.error("Unhandled exception [errorId={}]", errorId, ex);
        Map<String, Object> b = body(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again.");
        b.put("errorId", errorId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
    }
}
