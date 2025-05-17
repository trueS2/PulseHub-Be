package pulsehub.pulsehubbe.user.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import pulsehub.pulsehubbe.global.exception.GlobalException;
import pulsehub.pulsehubbe.global.exception.type.ErrorCode;
import pulsehub.pulsehubbe.user.dto.GithubUserDto;
import pulsehub.pulsehubbe.user.service.GithubUserService;

@Service
public class GithubUserServiceImpl implements GithubUserService {

    private static final String GITHUB_API_URL = "https://api.github.com/users/";

    private final RestTemplate restTemplate;

    public GithubUserServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public GithubUserDto getUser(String username) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    GITHUB_API_URL + username, Map.class
            );

            if (response == null || response.isEmpty()) {
                throw new GlobalException(ErrorCode.USER_NOT_FOUND);
            }

            return GithubUserDto.builder()
                    .login((String) response.get("login"))
                    .name((String) response.get("name"))
                    .avatarUrl((String) response.get("avatar_url"))
                    .followers((Integer) response.getOrDefault("followers", 0))
                    .following((Integer) response.getOrDefault("following", 0))
                    .htmlUrl((String) response.get("html_url"))
                    .build();

        } catch (RestClientException e) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
