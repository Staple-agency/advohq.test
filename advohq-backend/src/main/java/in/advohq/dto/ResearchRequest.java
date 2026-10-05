package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * A case-law / bare-act search request. Mirrors the fields collected by the
 * editor's "Research & Case Law AI" panel (advohq-frontend/editor.html).
 */
public record ResearchRequest(
        @NotBlank String scope,   // "Judgements" | "Bare Acts" | "Tribunals"
        String court,
        String searchPreference,
        @NotBlank String term,
        String fromDate,
        String toDate,
        String actTitle,
        String section,
        String party1,
        String party2,
        String judge
) {
}
