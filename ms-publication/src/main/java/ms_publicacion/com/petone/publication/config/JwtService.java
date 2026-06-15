package ms_publicacion.com.petone.publication.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "SeguridadSuperFuerteParaJWTPETONE";

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public String extractUsername(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String nombre = claims.get("nombre", String.class);
            
            System.out.println("DEBUG JwtService - Claims keys: " + claims.keySet());
            System.out.println("DEBUG JwtService - nombre claim: " + nombre);
            
            if (nombre != null && !nombre.trim().isEmpty()) {
                System.out.println("DEBUG JwtService - Retornando nombre del claim: " + nombre);
                return nombre;
            }
            
            String email = claims.getSubject();
            System.out.println("DEBUG JwtService - Retornando email como fallback: " + email);
            return email;
        } catch (Exception e) {
            System.out.println("ERROR JwtService extractUsername: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String extractUserApellido(String token) {
        Claims claims = extractAllClaims(token);
        String apellido = claims.get("apellido", String.class);
        return apellido != null ? apellido : "";
    }

    private Key getSignInKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
