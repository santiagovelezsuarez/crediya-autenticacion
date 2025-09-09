package co.pragma.api.security;

import co.pragma.model.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private final long expiration;
    private final SecretKey key;

    private static final String ROLE_CLAIM = "role";
    private static final String EMAIL_CLAIM = "email";
    private static final String USERID_CLAIM = "userId";
    private static final String PERMISSIONS_CLAIM = "permissions";

    public JwtService(@Value("${security.jwt.secret-key}") String secret, @Value("${security.jwt.expiration-time}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(ROLE_CLAIM, usuario.getRol().getNombre());
        claims.put(EMAIL_CLAIM, usuario.getEmail());
        claims.put(USERID_CLAIM, usuario.getId().toString());
        claims.put(PERMISSIONS_CLAIM, usuario.getRol().getPermissions());

        log.debug("Generando token para el usuario {} with {} permissions", usuario.getEmail(), usuario.getRol().getPermissions().size());

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
            final Date expirationDate = extractClaim(token, Claims::getExpiration);
            return !expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return extractClaim(token, claims -> (String) claims.get(ROLE_CLAIM));
    }

    public Set<String> getPermissionsFromToken(String token) {
        List<String> permissionsList = extractClaim(token,claims -> (List<String>) claims.get(PERMISSIONS_CLAIM));
        return permissionsList != null ? new HashSet<>(permissionsList) : Set.of();
    }
}
