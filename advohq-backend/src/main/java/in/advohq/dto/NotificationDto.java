package in.advohq.dto;

import java.time.LocalDate;

/**
 * One row in the notification panel, derived live from a schedule event rather
 * than stored. Deriving means editing or deleting an event updates the panel
 * immediately, with no generated rows left behind to go stale.
 *
 * @param group  "upcoming" or "recent" — which section it belongs to
 * @param type   the event type (hearing / meeting / deadline / filing), for the icon
 * @param when   human label already resolved server-side ("Today", "In 3 days",
 *               "2 days ago") so both the home and settings panels agree
 * @param unread true when the event was created after the user last cleared the bell
 */
public record NotificationDto(String id,
                              String group,
                              String type,
                              String title,
                              String body,
                              LocalDate date,
                              String when,
                              boolean unread) {}
