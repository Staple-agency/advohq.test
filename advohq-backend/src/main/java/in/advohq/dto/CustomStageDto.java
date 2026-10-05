package in.advohq.dto;

import in.advohq.domain.CustomStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CustomStageDto(
        UUID id,
        @NotBlank @Size(max = 80) String name,
        int sortOrder,
        /** Palette name; blank/unknown values fall back to "default" on save. */
        @Size(max = 20) String color
) {
    public static CustomStageDto from(CustomStage s) {
        return new CustomStageDto(s.getId(), s.getName(), s.getSortOrder(), s.getColor());
    }
}
