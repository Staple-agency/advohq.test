package in.advohq.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A stage in one case's own progress track. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class CaseProgressStage {
    @Column(name = "stage_name", nullable = false, length = 80)
    private String name;

    @Column(nullable = false, length = 20)
    private String color = "default";
}
