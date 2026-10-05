package in.advohq.dto;

import in.advohq.domain.Note;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

/**
 * A note as sent to and from the editor. {@code id} and {@code createdAt} are
 * ignored on create.
 */
public record NoteDto(
        UUID id,
        UUID documentId,
        @NotBlank @Size(max = 10_000) String text,
        Integer pageIndex,
        @Size(max = 60) String pageLabel,
        Instant createdAt
) {
    public static NoteDto from(Note n) {
        return new NoteDto(n.getId(), n.getDocumentId(), n.getBody(), n.getPageIndex(), n.getPageLabel(), n.getCreatedAt());
    }
}
