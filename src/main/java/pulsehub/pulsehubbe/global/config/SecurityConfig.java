package pulsehub.pulsehubbe.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import pulsehub.pulsehubbe.auth.handler.OAuthSuccessHandler;

@Configuration
public class SecurityConfig {

    private final OAuthSuccessHandler successHandler;

    public SecurityConfig(OAuthSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(successHandler)
                );

        return http.build();
    }
}
