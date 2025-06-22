package pulsehub.pulsehubbe.activity.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import pulsehub.pulsehubbe.global.exception.GlobalException;
import pulsehub.pulsehubbe.global.exception.type.ErrorCode;

@Component
public class GitHubApiClient {

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> fetchUserRepos(String username, String accessToken) {
        String authenticated = fetchAuthenticatedUsername(accessToken);
        String url;
        if (username.equals(authenticated)) {
            url = GITHUB_API_BASE + "/user/repos?per_page=100";
        } else {
            url = GITHUB_API_BASE + "/users/" + username + "/repos?per_page=100";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Repo[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Repo[].class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new GlobalException(ErrorCode.REPO_NOT_FOUND);
            }

            List<String> repoNames = new ArrayList<>();
            for (Repo repo : response.getBody()) {
                if (!repo.isFork) {
                    repoNames.add(repo.name);
                }
            }
            return repoNames;

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GlobalException(ErrorCode.REPO_NOT_FOUND, e);
            }
            throw new GlobalException(ErrorCode.GITHUB_API_ERROR, e);
        } catch (RestClientException e) {
            throw new GlobalException(ErrorCode.GITHUB_API_ERROR, e);
        }
    }


    private String fetchAuthenticatedUsername(String accessToken) {
        String url = GITHUB_API_BASE + "/user";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, User.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().login;
            } else {
                throw new GlobalException(ErrorCode.GITHUB_API_ERROR);
            }
        } catch (RestClientException e) {
            throw new GlobalException(ErrorCode.GITHUB_API_ERROR, e);
        }
    }

    public Map<LocalDate, Integer> fetchCommitCounts(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            String accessToken
    ) {
        List<String> repos = fetchUserRepos(username, accessToken);
        Map<LocalDate, Integer> commitCountMap = new HashMap<>();
        for (String repo : repos) {
            fetchCommitsForRepo(username, repo, startDate, endDate, commitCountMap, null, accessToken);
        }
        return commitCountMap;
    }

    public Map<Integer, Integer> fetchHourlyCommitCounts(
            String username,
            LocalDate startDate,
            LocalDate endDate,
            String accessToken
    ) {
        List<String> repos = fetchUserRepos(username, accessToken);
        Map<Integer, Integer> hourlyCommitMap = new HashMap<>();
        for (String repo : repos) {
            fetchCommitsForRepo(username, repo, startDate, endDate, null, hourlyCommitMap, accessToken);
        }
        return hourlyCommitMap;
    }

    private void fetchCommitsForRepo(
            String owner,
            String repo,
            LocalDate startDate,
            LocalDate endDate,
            Map<LocalDate, Integer> dailyMap,
            Map<Integer, Integer> hourlyMap,
            String accessToken
    ) {
        OffsetDateTime sinceDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime untilDateTime = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        String sinceParam = sinceDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String untilParam = untilDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String url = String.format(
                GITHUB_API_BASE + "/repos/%s/%s/commits?since=%s&until=%s&per_page=100",
                owner, repo, sinceParam, untilParam
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Commit[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Commit[].class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                for (Commit commit : response.getBody()) {
                    OffsetDateTime odt = OffsetDateTime.parse(commit.commit.author.date);
                    LocalDate date = odt.toLocalDate();
                    int hour = odt.getHour();

                    if (dailyMap != null) {
                        dailyMap.put(date, dailyMap.getOrDefault(date, 0) + 1);
                    }
                    if (hourlyMap != null) {
                        hourlyMap.put(hour, hourlyMap.getOrDefault(hour, 0) + 1);
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                // 빈 리포지토리인 경우에만 스킵
                System.out.println("❗ 빈 리포지토리 스킵: " + owner + "/" + repo);
                return;
            }
            throw new GlobalException(ErrorCode.GITHUB_API_ERROR, e);
        } catch (RestClientException e) {
            throw new GlobalException(ErrorCode.GITHUB_API_ERROR, e);
        }
    }

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

    static class User {
        public String login;
    }
}
