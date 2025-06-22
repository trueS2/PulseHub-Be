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
        System.out.println("🔓 파싱된 JWT: " + jwt);

        String githubAccessToken = jwtProvider.getAccessTokenFromJwt(jwt);
        System.out.println("🐙 추출된 GitHub access token: " + githubAccessToken);

        System.out.println("🔑 받은 Authorization 헤더: " + authorizationHeader);

        return commitAnalysisService.getCommitCounts(username, days, githubAccessToken);
    }
}
