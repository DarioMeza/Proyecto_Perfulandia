package com.perfulandia.UsuariosService.security;

import com.perfulandia.UsuariosService.model.Usuario;
import io.jsonwebtoken.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "clave-super-secreta-que-debes-cambiar";

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getCorreo())
                .claim("rol", usuario.getRol())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (JwtException e) {
            throw new JwtException("Token inválido al extraer el usuario: " + e.getMessage(), e);
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            throw new JwtException("Token inválido: " + e.getMessage(), e);
        }
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expirado", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Token no soportado", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Token mal formado", e);
        } catch (SignatureException e) {
            throw new JwtException("Firma del token inválida", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Token vacío o nulo", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            throw new JwtException("Error al verificar expiración: " + e.getMessage(), e);
        }
    }
}
