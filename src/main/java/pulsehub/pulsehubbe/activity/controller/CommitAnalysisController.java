package pulsehub.pulsehubbe.activity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pulsehub.pulsehubbe.activity.dto.CommitAnalysisResponse;
import pulsehub.pulsehubbe.activity.service.CommitAnalysisService;
import pulsehub.pulsehubbe.auth.jwt.JwtProvider;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class CommitAnalysisController {

    private final CommitAnalysisService commitAnalysisService;
    private final JwtProvider jwtProvider;

    @GetMapping("/commits")
    public CommitAnalysisResponse getCommitAnalysis(
            @RequestParam String username,
            @RequestParam int days,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwt = authorizationHeader.substring(7);

        String githubAccessToken = jwtProvider.getAccessTokenFromJwt(jwt);

        return commitAnalysisService.getCommitCounts(username, days, githubAccessToken);
    }
}
