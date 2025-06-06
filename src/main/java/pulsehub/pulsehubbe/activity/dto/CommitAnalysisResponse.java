package pulsehub.pulsehubbe.activity.dto;

import java.time.LocalDate;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommitAnalysisResponse {
    private Map<LocalDate, Integer> dailyCommitCounts;
}
