package in.advohq.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/** A note from the editor's Notes panel, optionally tied to a document page. */
@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Null for a note written with no document open. */
    @Column(name = "document_id")
    private UUID documentId;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    /** Zero-based page within the document, for jumping back to it. */
    @Column(name = "page_index")
    private Integer pageIndex;

    /** The page label shown on the card, e.g. "Page 3 of 12". */
    @Column(name = "page_label", length = 60)
    private String pageLabel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
