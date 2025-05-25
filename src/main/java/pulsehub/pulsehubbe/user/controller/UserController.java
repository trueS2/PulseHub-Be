package pulsehub.pulsehubbe.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pulsehub.pulsehubbe.user.dto.GithubUserDto;
import pulsehub.pulsehubbe.user.service.GithubUserService;

@RestController
public class UserController {

    private final GithubUserService githubUserService;

    public UserController(GithubUserService githubUserService) {
        this.githubUserService = githubUserService;
    }

    @GetMapping("/api/github/user/{username}")
    public GithubUserDto getUser(@PathVariable String username) {
        return githubUserService.getUser(username);
    }
}
