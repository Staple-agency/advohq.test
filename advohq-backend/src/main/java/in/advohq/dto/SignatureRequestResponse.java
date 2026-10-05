package in.advohq.dto;

import in.advohq.domain.SignatureRequestEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SignatureRequestResponse(UUID id, String documentName, String status,
                                        Instant createdAt, Instant sentAt, Instant completedAt,
                                        List<SignerResponse> signers) {
    public static SignatureRequestResponse from(SignatureRequestEntity e) {
        return new SignatureRequestResponse(e.getId(), e.getDocumentName(), e.getStatus(),
                e.getCreatedAt(), e.getSentAt(), e.getCompletedAt(),
                e.getSigners().stream().map(SignerResponse::from).toList());
    }
}
