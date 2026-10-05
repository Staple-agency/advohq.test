package in.advohq.repo;

import in.advohq.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findByUserIdAndDocumentIdInOrderByCreatedAtDesc(UUID userId, Collection<UUID> documentIds);

    List<Note> findByUserIdAndDocumentIdIsNullOrderByCreatedAtDesc(UUID userId);

    Optional<Note> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}
