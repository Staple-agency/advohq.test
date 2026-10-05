package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * A raw-retrieval request for grounding Draft AI (advohq-frontend/api/chat.js)
 * in indexed case law — see {@code POST /api/research/context}. Unlike
 * {@link ResearchRequest} (the Research & Case Law AI panel's structured
 * search form), this is just free text: the user's chat message, embedded
 * and matched directly, searching both judgments and legislation.
 */
public record ContextRequest(@NotBlank String term) {
}
