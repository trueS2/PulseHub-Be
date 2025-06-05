package pulsehub.pulsehubbe.activity.service;

import pulsehub.pulsehubbe.activity.dto.CommitAnalysisResponse;

public interface CommitAnalysisService {
    CommitAnalysisResponse getCommitCounts(String username, int days);
}
