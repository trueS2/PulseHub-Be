package pulsehub.pulsehubbe.activity.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulsehub.pulsehubbe.activity.dto.CommitAnalysisResponse;
import pulsehub.pulsehubbe.activity.dto.CommitTag;
import pulsehub.pulsehubbe.activity.service.CommitAnalysisService;
import pulsehub.pulsehubbe.activity.util.CommitTagAnalyzer;
import pulsehub.pulsehubbe.activity.util.GitHubApiClient;

@Service
@RequiredArgsConstructor
public class CommitAnalysisServiceImpl implements CommitAnalysisService {

    private final GitHubApiClient gitHubApiClient;

    @Override
    public CommitAnalysisResponse getCommitCounts(String username, int days, String accessToken) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 1. 일별 커밋 수
        Map<LocalDate, Integer> commitCountMap = gitHubApiClient.fetchCommitCounts(username, startDate, endDate, accessToken);
        Map<LocalDate, Integer> fullDateMap = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            fullDateMap.put(date, commitCountMap.getOrDefault(date, 0));
        }

        // 2. 시간대별 커밋 수
        Map<Integer, Integer> hourlyCommitMap = gitHubApiClient.fetchHourlyCommitCounts(username, startDate, endDate, accessToken);

        // 3. 가장 활발한 시간대 계산
        int peakHour = 0;
        int peakCount = 0;
        for (Map.Entry<Integer, Integer> entry : hourlyCommitMap.entrySet()) {
            if (entry.getValue() > peakCount) {
                peakHour = entry.getKey();
                peakCount = entry.getValue();
            }
        }

        List<CommitTag> tags = CommitTagAnalyzer.analyzeTags(fullDateMap, hourlyCommitMap);

        return new CommitAnalysisResponse(fullDateMap, peakHour, peakCount, tags);
    }
}
