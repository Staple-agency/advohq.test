package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

public record SignatureRequestCreateRequest(@NotBlank String documentName) {
}
