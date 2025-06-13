package pulsehub.pulsehubbe.activity.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubApiClient {

    private static final String GITHUB_API_BASE = "https://api.github.com";

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> fetchUserRepos(String username) {
        String url = GITHUB_API_BASE + "/users/" + username + "/repos?per_page=100";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Repo[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repo[].class);

        List<String> repoNames = new ArrayList<>();
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            for (Repo repo : response.getBody()) {
                if (!repo.isFork) {
                    repoNames.add(repo.name);
                }
            }
        }
        return repoNames;
    }

    public Map<LocalDate, Integer> fetchCommitCounts(String username, LocalDate startDate, LocalDate endDate) {
        List<String> repos = fetchUserRepos(username);
        Map<LocalDate, Integer> commitCountMap = new HashMap<>();

        for (String repo : repos) {
            fetchCommitsForRepo(username, repo, startDate, endDate, commitCountMap, null);
        }
        return commitCountMap;
    }

    public Map<Integer, Integer> fetchHourlyCommitCounts(String username, LocalDate startDate, LocalDate endDate) {
        List<String> repos = fetchUserRepos(username);
        Map<Integer, Integer> hourlyCommitMap = new HashMap<>();

        for (String repo : repos) {
            fetchCommitsForRepo(username, repo, startDate, endDate, null, hourlyCommitMap);
        }
        return hourlyCommitMap;
    }

    private void fetchCommitsForRepo(
            String owner,
            String repo,
            LocalDate startDate,
            LocalDate endDate,
            Map<LocalDate, Integer> dailyMap,
            Map<Integer, Integer> hourlyMap) {

        OffsetDateTime sinceDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime untilDateTime = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        String sinceParam = sinceDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String untilParam = untilDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String url = String.format(
                GITHUB_API_BASE + "/repos/%s/%s/commits?since=%s&until=%s&per_page=100",
                owner, repo, sinceParam, untilParam
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Commit[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Commit[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                for (Commit commit : response.getBody()) {
                    String dateStr = commit.commit.author.date;
                    OffsetDateTime odt = OffsetDateTime.parse(dateStr);
                    LocalDate date = odt.toLocalDate();
                    int hour = odt.getHour(); // 0 ~ 23

                    if (dailyMap != null) {
                        dailyMap.put(date, dailyMap.getOrDefault(date, 0) + 1);
                    }

                    if (hourlyMap != null) {
                        hourlyMap.put(hour, hourlyMap.getOrDefault(hour, 0) + 1);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("오류 발생 - fetchCommitsForRepo: " + e.getMessage());
            throw e;
        }
    }

    // 내부 클래스: GitHub API 응답용
    static class Repo {
        public String name;
        public boolean isFork;
    }

    static class Commit {
        public CommitDetail commit;

        static class CommitDetail {
            public Author author;

            static class Author {
                public String date;
            }
        }
    }
}
