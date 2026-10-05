package in.advohq.dto;

import in.advohq.domain.DocumentOcr;

import java.util.UUID;

/**
 * OCR progress for one document. {@code text} is filled only when the caller
 * asks for the finished text, so polling stays cheap.
 */
public record OcrStatusResponse(
        UUID documentId,
        String status,
        String language,
        int pagesTotal,
        int pagesDone,
        boolean truncated,
        String text,
        String error
) {
    public static OcrStatusResponse progress(DocumentOcr o) {
        return new OcrStatusResponse(o.getDocumentId(), o.getStatus().name(), o.getLanguage(),
                o.getPagesTotal(), o.getPagesDone(), o.isTruncated(), null, o.getErrorMessage());
    }

    public static OcrStatusResponse withText(DocumentOcr o) {
        return new OcrStatusResponse(o.getDocumentId(), o.getStatus().name(), o.getLanguage(),
                o.getPagesTotal(), o.getPagesDone(), o.isTruncated(),
                o.getExtractedText(), o.getErrorMessage());
    }

    /** No OCR has ever been requested for this document. */
    public static OcrStatusResponse none(UUID documentId) {
        return new OcrStatusResponse(documentId, "NONE", null, 0, 0, false, null, null);
    }
}
