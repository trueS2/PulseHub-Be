package pulsehub.pulsehubbe.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GithubUserDto {

    private String login;
    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private int followers;
    private int following;

    @JsonProperty("html_url")
    private String htmlUrl;
}
