package in.advohq.config;

import in.advohq.security.AuthRateLimitFilter;
import in.advohq.security.JwtAuthenticationFilter;
import in.advohq.security.SignatureRateLimitFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(AdvoHqProperties.class)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthRateLimitFilter rateLimitFilter;
    private final SignatureRateLimitFilter signatureRateLimitFilter;
    private final AdvoHqProperties props;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter,
                          AuthRateLimitFilter rateLimitFilter,
                          SignatureRateLimitFilter signatureRateLimitFilter,
                          AdvoHqProperties props) {
        this.jwtFilter = jwtFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.signatureRateLimitFilter = signatureRateLimitFilter;
        this.props = props;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)   // stateless JWT API — no CSRF tokens
            // Security response headers. Spring Security already sets nosniff,
            // X-Frame-Options: DENY, and no-store Cache-Control by default; here
            // we make HSTS explicit and add the two headers it does NOT set by
            // default — Referrer-Policy and a restrictive Permissions-Policy.
            .headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .maxAgeInSeconds(31536000))   // 1 year
                .addHeaderWriter(new StaticHeadersWriter(
                        "Referrer-Policy", "strict-origin-when-cross-origin"))
                .addHeaderWriter(new StaticHeadersWriter(
                        "Permissions-Policy", "geolocation=(), camera=(), microphone=(), payment=()")))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/auth/**",
                        "/actuator/health",
                        "/error",
                        // Manual reminder trigger — the controller enforces its
                        // own shared-secret header check, so JWT is not needed.
                        "/api/admin/reminders/run",
                        // Razorpay calls this server-to-server with no bearer
                        // token; its credential is the HMAC signature over the
                        // raw body, which SubscriptionController verifies before
                        // acting on anything.
                        "/api/subscriptions/webhook",
                        // OneDrive OAuth browser navigations — no Authorization
                        // header is possible on a top-level redirect. /login is
                        // authenticated by a single-use ticket minted by the
                        // JWT-protected /api/onedrive/authorize; /callback by the
                        // OAuth state + HttpOnly nonce cookie. OneDriveService
                        // verifies both. Every other /api/onedrive/** route
                        // still requires a JWT.
                        "/api/onedrive/login",
                        "/api/onedrive/callback",
                        // Public e-signature page (advohq-frontend/sign.html) — the signer has
                        // no AdvoHQ account; the per-signer token in the path is the credential,
                        // verified inside SignatureService.
                        "/api/signatures/sign/**",
                        // Brevo inbound email (scan to email) calls this server-to-server;
                        // ScanInboxController checks the shared webhook secret itself.
                        "/api/scan-inbox/webhook"
                ).permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitFilter, JwtAuthenticationFilter.class)
            .addFilterBefore(signatureRateLimitFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origins = Arrays.stream(props.security().cors().allowedOrigins().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        // Content-Disposition: the editor reads the original filename from
        // the /api/documents/{id}/content response.
        cfg.setExposedHeaders(List.of("Location", "Content-Disposition"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
