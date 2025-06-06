package pulsehub.pulsehubbe.activity.controller;

import org.springframework.web.bind.annotation.*;
import pulsehub.pulsehubbe.activity.service.CommitAnalysisService;
import pulsehub.pulsehubbe.activity.dto.CommitAnalysisResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class CommitAnalysisController {

    private final CommitAnalysisService commitAnalysisService;

    @GetMapping("/commits")
    public CommitAnalysisResponse getCommitAnalysis(
            @RequestParam String username,
            @RequestParam int days) {
        return commitAnalysisService.getCommitCounts(username, days);
    }
}
