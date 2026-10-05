package in.advohq.dto;

import in.advohq.domain.CaseProgressStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CaseProgressStageDto(
        @NotBlank @Size(max = 80) String name,
        @Size(max = 20) String color
) {
    public static CaseProgressStageDto from(CaseProgressStage stage) {
        return new CaseProgressStageDto(stage.getName(), stage.getColor());
    }
}
