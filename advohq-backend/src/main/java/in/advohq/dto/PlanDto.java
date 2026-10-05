package in.advohq.dto;

import in.advohq.config.PlanCatalog;

import java.util.List;

/**
 * A plan as shown on the pricing page. Amount is paise; the UI divides by 100.
 * {@code highlights} is the server's feature list, so the page can't drift from
 * what's actually enforced.
 */
public record PlanDto(String code, String name, String description, long amountPaise,
                      long storageBytes, List<String> highlights, boolean available) {

    public static PlanDto from(PlanCatalog.Plan plan, boolean available) {
        return new PlanDto(plan.code(), plan.name(), plan.description(), plan.amountPaise(),
                plan.storageBytes(), plan.highlights(), available);
    }
}
