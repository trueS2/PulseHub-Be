package pulsehub.pulsehubbe.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import pulsehub.pulsehubbe.auth.jwt.JwtProvider;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService clientService;
    private final JwtProvider jwtProvider;

    public OAuthSuccessHandler(OAuth2AuthorizedClientService clientService, JwtProvider jwtProvider) {
        this.clientService = clientService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        String githubAccessToken = client.getAccessToken().getTokenValue();

        String jwt = jwtProvider.createToken(githubAccessToken);

        response.sendRedirect("http://localhost:3000/oauth/redirect?jwt=" + jwt);
    }
}
