package pulsehub.pulsehubbe.auth.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public String createToken(String githubAccessToken) {
        return Jwts.builder()
                .setSubject("github-user")
                .claim("accessToken", githubAccessToken)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String getAccessTokenFromJwt(String jwt) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt)
                .getBody();

        return claims.get("accessToken", String.class);
    }
}
