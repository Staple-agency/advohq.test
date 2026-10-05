package in.advohq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The server-side price list and what each plan unlocks.
 *
 * <p>Prices live here and <em>only</em> here. The browser sends a plan code, never
 * an amount — otherwise a user could open devtools and subscribe for Rs 1. The
 * authoritative charge amount is the one configured on the Razorpay plan itself;
 * {@code amountPaise} below is what we display and store for our own records, so
 * it must be kept equal to the Razorpay plan's amount.
 *
 * <p>Likewise the storage limit and feature set are enforced server-side
 * (DocumentService for storage, the frontend server's AI proxy via
 * {@code /api/subscriptions/entitlements} for AI), so hiding a button in the UI
 * is a convenience, never the control.
 *
 * <p>Razorpay plan ids come from the dashboard (Subscriptions &rarr; Plans) and are
 * injected per environment, so test and live keys can point at different plans.
 */
@Component
public class PlanCatalog {

    private static final long GB = 1024L * 1024 * 1024;

    /** Paid-for capabilities beyond viewing/editing files. */
    public enum Feature {
        /** Advo AI preset questions (quick actions) and the preset risk analysis. */
        AI_PRESETS,
        /** Advo AI free-form questions ("ask anything"). */
        AI_FREEFORM,
        /** The Document Intelligence "Analyze document" panel. */
        DOC_INTEL,
        /** Sarvam translation / transcription. */
        SARVAM,
        /** Research & Case Law AI (judgments, bare acts, tribunal rulings). */
        RESEARCH_AI,
        /** Create and send e-signature requests. */
        E_SIGNATURE
    }

    /**
     * A purchasable plan. {@code amountPaise} is in paise: 79900 = Rs 799.
     * {@code highlights} is the feature list shown on the pricing page.
     */
    public record Plan(String code, String name, String description, long amountPaise,
                       long storageBytes, Set<Feature> features, List<String> highlights,
                       String razorpayPlanId) {
        /** Rupees, for display only — never use this for arithmetic. */
        public String displayPrice() {
            return "%,d".formatted(amountPaise / 100);
        }
    }

    /** Trial = Premium's features and storage, for a limited time. */
    public static final String TRIAL_CODE = "TRIAL";
    public static final String PREMIUM_CODE = "PREMIUM";

    private final Map<String, Plan> plans = new LinkedHashMap<>();

    public PlanCatalog(@Value("${advohq.razorpay.plans.basic:}") String basicPlanId,
                       @Value("${advohq.razorpay.plans.professional:}") String professionalPlanId,
                       @Value("${advohq.razorpay.plans.premium:}") String premiumPlanId) {
        register(new Plan("BASIC", "Basic",
                "View and edit your case files",
                79_900L, 15 * GB, EnumSet.noneOf(Feature.class),
                List.of("View & edit PDF and Word files", "15 GB document storage",
                        "Schedule & hearing reminders", "Invoice generator",
                        "OneDrive import & scanning", "eCourts case lookup"),
                basicPlanId));
        register(new Plan("PROFESSIONAL", "Professional",
                "Adds Advo AI quick actions and Sarvam AI",
                289_900L, 50 * GB, EnumSet.of(Feature.AI_PRESETS, Feature.SARVAM),
                List.of("Everything in Basic", "50 GB document storage",
                        "Advo AI quick actions (summarise, key clauses, action items, draft a response)",
                        "Sarvam AI translation & transcription"),
                professionalPlanId));
        register(new Plan(PREMIUM_CODE, "Premium",
                "Full access to AdvoHQ",
                359_900L, 200 * GB, EnumSet.allOf(Feature.class),
                List.of("Everything in Professional", "200 GB document storage",
                        "Ask Advo AI anything", "Document Intelligence analysis",
                        "Research & Case Law AI", "E-signature requests"),
                premiumPlanId));
    }

    private void register(Plan p) {
        plans.put(p.code(), p);
    }

    /** All plans, in display order. Includes plans whose Razorpay id isn't configured yet. */
    public List<Plan> all() {
        return List.copyOf(plans.values());
    }

    /**
     * Look up a plan by the code the browser submitted (or a stored code).
     * Empty for anything unrecognised. "FIRM" is the pre-three-tier name of the
     * top plan and resolves to Premium so any row written before the rename
     * keeps its access.
     */
    public Optional<Plan> find(String code) {
        if (code == null) return Optional.empty();
        String c = code.trim().toUpperCase();
        if ("FIRM".equals(c)) c = PREMIUM_CODE;
        return Optional.ofNullable(plans.get(c));
    }

    /** True once a Razorpay plan id has been configured for this plan. */
    public boolean isPurchasable(Plan plan) {
        return StringUtils.hasText(plan.razorpayPlanId());
    }
}
