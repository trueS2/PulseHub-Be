package pulsehub.pulsehubbe.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommitTag {
    private String code;
    private String label;
    private String description;
}
