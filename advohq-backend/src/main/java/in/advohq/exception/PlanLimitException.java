package in.advohq.exception;

/**
 * The caller's plan doesn't allow this (trial over, storage full, feature not
 * in plan). Mapped to 402 with a machine-readable {@code code} so the UI can
 * point at the subscription page. Deliberately not 401/403, which the frontend
 * treats as "logged out".
 */
public class PlanLimitException extends RuntimeException {

    public static final String PLAN_REQUIRED = "PLAN_REQUIRED";
    public static final String STORAGE_FULL = "STORAGE_FULL";

    private final String code;

    public PlanLimitException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String code() {
        return code;
    }
}
