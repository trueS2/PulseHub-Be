package pulsehub.pulsehubbe.user.service;

import pulsehub.pulsehubbe.user.dto.GithubUserDto;

public interface GithubUserService {
    GithubUserDto getUser(String username);
}
