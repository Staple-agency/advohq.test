package in.advohq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly-typed binding for all {@code advohq.*} configuration in application.yml.
 */
@ConfigurationProperties(prefix = "advohq")
public record AdvoHqProperties(Security security, Aws aws, Mail mail, Reminders reminders, Frontend frontend) {

    public record Security(Jwt jwt, Cors cors) {
        public record Jwt(String secret, long expirationMs) {}
        public record Cors(String allowedOrigins) {}
    }

    public record Aws(String region, S3 s3) {
        public record S3(String bucket, String endpoint, long presignExpiryMinutes) {}
    }

    /** Brevo transactional email — same provider/account as advohq-frontend's OTP emails. */
    public record Mail(String brevoApiKey, String senderEmail, String senderName) {}

    /** When the day-before event reminder job runs. */
    public record Reminders(String cron, String zone) {}

    /** Base URL of the deployed advohq-frontend, for links this service builds and emails out
     *  (e.g. a signature request's signing link). */
    public record Frontend(String baseUrl) {}
}
