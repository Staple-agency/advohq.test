package in.advohq.dto;

import in.advohq.domain.SignatureField;

import java.util.UUID;

public record FieldResponse(UUID id, String fieldType) {
    public static FieldResponse from(SignatureField f) {
        return new FieldResponse(f.getId(), f.getFieldType());
    }
}
