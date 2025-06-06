package pulsehub.pulsehubbe.activity.service.impl;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulsehub.pulsehubbe.activity.dto.CommitAnalysisResponse;
import pulsehub.pulsehubbe.activity.service.CommitAnalysisService;
import pulsehub.pulsehubbe.activity.util.GitHubApiClient;

@Service
@RequiredArgsConstructor
public class CommitAnalysisServiceImpl implements CommitAnalysisService {

    private final GitHubApiClient gitHubApiClient;

    @Override
    public CommitAnalysisResponse getCommitCounts(String username, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        Map<LocalDate, Integer> commitCountMap = gitHubApiClient.fetchCommitCounts(username, startDate, endDate);

        Map<LocalDate, Integer> fullDateMap = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            fullDateMap.put(date, commitCountMap.getOrDefault(date, 0));
        }

        return new CommitAnalysisResponse(fullDateMap);
    }
}
