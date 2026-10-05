package in.advohq.dto;

/** One search hit: a judgment, bare-act section, or tribunal ruling. */
public record ResearchResult(String title, String meta, String note, String sourceUrl) {
}
