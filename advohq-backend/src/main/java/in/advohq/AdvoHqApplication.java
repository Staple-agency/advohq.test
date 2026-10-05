package in.advohq;

import in.advohq.service.EventReminderScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdvoHqApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvoHqApplication.class, args);
    }

    /**
     * Global lazy initialization (spring.main.lazy-initialization=true) speeds
     * up cold-start boot, but it would also stop the reminder scheduler's
     * {@code @Scheduled} cron and {@code ApplicationReadyEvent} catch-up from
     * ever registering — a lazy bean isn't created until something asks for it,
     * and nothing does. Force just that bean to initialize eagerly so the
     * scheduling still wires up at startup.
     */
    @Bean
    static LazyInitializationExcludeFilter eagerScheduler() {
        return LazyInitializationExcludeFilter.forBeanTypes(EventReminderScheduler.class);
    }
}
