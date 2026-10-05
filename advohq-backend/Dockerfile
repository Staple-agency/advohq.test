# ── Build stage ──────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ── Runtime stage ────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre
WORKDIR /app

# Run as an unprivileged user, never root. If the process is ever
# compromised (RCE, path traversal, a vulnerable dependency), it has no
# root inside the container and a far smaller blast radius on the host.
# PDFBox (used to split scanned PDFs into 10-page chunks for OCR) initialises
# AWT classes even though it never renders anything, and AWT wants fontconfig.
RUN apt-get update \
 && apt-get install -y --no-install-recommends fontconfig libfreetype6 \
 && rm -rf /var/lib/apt/lists/*

RUN groupadd --system --gid 10001 advohq \
 && useradd  --system --uid 10001 --gid advohq --home-dir /app --shell /usr/sbin/nologin advohq

COPY --from=build /app/target/advohq-backend-1.0.0.jar app.jar
# Own the app dir as the non-root user so nothing needs to be writable by root.
RUN chown -R advohq:advohq /app

USER advohq:advohq

EXPOSE 8080

# Container-aware JVM tuning:
#   MaxRAMPercentage    — size the heap from the *container* memory limit
#                         (Render/K8s cgroup), not the host's total RAM, so
#                         the JVM never gets OOM-killed for overshooting.
#   ExitOnOutOfMemoryError — fail fast on OOM so the orchestrator restarts a
#                         clean instance instead of limping in a bad state.
#   -Djava.security.egd — non-blocking entropy source; avoids slow startup
#                         when the container's entropy pool is thin.
#   TieredStopAtLevel=1 — stop JIT at the C1 compiler. Skipping the expensive
#                         C2 compilation slashes cold-start/boot time, which is
#                         what the login latency actually pays for on the
#                         free tier (the instance spins down when idle). Peak
#                         throughput is marginally lower — irrelevant for this
#                         low-traffic API, decisive for wake-up speed.
#   UseSerialGC         — single-threaded GC. On the free tier's fractional CPU
#                         and small heap it starts faster and adds no GC-thread
#                         overhead versus G1.
#   java.awt.headless   — there is no display in a container; PDFBox pulls in
#                         AWT classes, which must not try to open one.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom -XX:TieredStopAtLevel=1 -XX:+UseSerialGC -Djava.awt.headless=true"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
