package in.advohq.repo;

import in.advohq.domain.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<ScheduleEvent, UUID> {

    List<ScheduleEvent> findByUserIdOrderByEventDateAsc(UUID userId);

    List<ScheduleEvent> findByUserIdAndEventDateBetweenOrderByEventDateAsc(
            UUID userId, LocalDate from, LocalDate to);

    Optional<ScheduleEvent> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Events falling in the given (inclusive) date window whose reminder hasn't
     * gone out yet. The scheduler passes [today, tomorrow] rather than just
     * tomorrow: on a free-tier host that sleeps through the 7 AM cron, a
     * day-before reminder that was never sent would otherwise be lost forever
     * once the event became "today". This lets a late run still deliver it.
     */
    List<ScheduleEvent> findByEventDateBetweenAndReminderSentAtIsNullOrderByEventDateAsc(
            LocalDate from, LocalDate to);

    /** Next few events from {@code from} onwards — the "Upcoming" half of the notification panel. */
    List<ScheduleEvent> findTop12ByUserIdAndEventDateGreaterThanEqualOrderByEventDateAsc(
            UUID userId, LocalDate from);

    /** Most recent events before {@code before} — the "Recent" half of the notification panel. */
    List<ScheduleEvent> findTop8ByUserIdAndEventDateLessThanOrderByEventDateDesc(
            UUID userId, LocalDate before);
}
