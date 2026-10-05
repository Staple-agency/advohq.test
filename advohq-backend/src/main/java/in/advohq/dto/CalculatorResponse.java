package in.advohq.dto;

import java.util.List;

/** Result of a suit-valuation / court-fee calculation. */
public record CalculatorResponse(
        String suitTypeLabel,
        double suitValuation,
        Double courtFeeBasis,
        double courtFee,
        String court,
        String territorialBasis,
        String commercialTrack,
        List<String> ruleTrace,
        List<String> warnings,
        List<LegalSource> legalSources
) {
    public record LegalSource(String title, String url, String note) {}
}
