package in.advohq.dto;

import in.advohq.domain.SignatureSigner;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SignerResponse(UUID id, String name, String email, boolean isCompany,
                              String status, Instant signedAt, List<FieldResponse> fields) {
    public static SignerResponse from(SignatureSigner s) {
        return new SignerResponse(s.getId(), s.getName(), s.getEmail(), s.isCompany(),
                s.getStatus(), s.getSignedAt(),
                s.getFields().stream().map(FieldResponse::from).toList());
    }
}
