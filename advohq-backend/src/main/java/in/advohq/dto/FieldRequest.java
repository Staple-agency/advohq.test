package in.advohq.dto;

import jakarta.validation.constraints.NotBlank;

/** fieldType: "Signature" | "Initials" | "Date" (case-insensitive; normalised server-side). */
public record FieldRequest(@NotBlank String fieldType) {
}
