package co.pragma.api.security;

import co.pragma.model.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final long expiration;
    private final SecretKey key;

    public JwtService(@Value("${security.jwt.secret-key}") String secret, @Value("${security.jwt.expiration-time}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("rol", usuario.getRol().getNombre());
        claims.put("email", usuario.getEmail());
        claims.put("userId", usuario.getId().toString());

        return createToken(claims, usuario.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token) {
        try {
            final Date expiration = extractClaim(token, Claims::getExpiration);
            return !expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return extractClaim(token, claims -> (String) claims.get("rol"));
    }
}
