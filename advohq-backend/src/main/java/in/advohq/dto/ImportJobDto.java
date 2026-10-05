package in.advohq.dto;

import in.advohq.domain.ImportJob;

import java.util.UUID;

/**
 * Progress of a background import. The page polls this and, on DONE, reloads
 * the document named by {@code documentId}.
 */
public record ImportJobDto(
        UUID id,
        String status,
        String fileName,
        long totalBytes,
        long copiedBytes,
        UUID documentId,
        String error
) {
    public static ImportJobDto from(ImportJob j) {
        return new ImportJobDto(j.getId(), j.getStatus().name(), j.getFileName(),
                j.getTotalBytes(), j.getCopiedBytes(), j.getDocumentId(), j.getErrorMessage());
    }
}
