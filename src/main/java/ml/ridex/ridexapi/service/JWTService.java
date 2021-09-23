package ml.ridex.ridexapi.service;

import io.jsonwebtoken.*;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.InvalidJwtAuthenticationException;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

@Service
public class JWTService {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final JwtParser jwtParser;
    private final long validityInMilliseconds; // 1h

    @Autowired
    public JWTService(@Value("${PRIVATE_KEY}") String rawPrivateKey, @Value("${PUBLIC_KEY}") String rawPublicKey,
                           @Value("${TOKEN_VALIDITY}") long tokenValidity) throws Exception {
        privateKey = loadPrivateKey(rawPrivateKey);
        publicKey = loadPublicKey(rawPublicKey);
        validityInMilliseconds = tokenValidity;
        jwtParser = Jwts.parserBuilder().setSigningKey(publicKey).build();
    }

    public String createToken(String phone, Role role) {
        Claims claims = Jwts.claims().setSubject(phone);
        claims.put("role", role);
        claims.put("type", "auth");
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(privateKey, SignatureAlgorithm.RS256)//
                .compact();
    }

    public String getUsername(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public Role getUserRole(String token) {
        return Role.valueOf((String)jwtParser.parseClaimsJws(token).getBody().get("role"));
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = jwtParser.parseClaimsJws(token);
            //Integer userId = Integer.parseInt(claims.getBody().getSubject());
            Boolean isTokenValid = !claims.getBody().getExpiration().before(new Date());
            return isTokenValid;
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }

    private PrivateKey loadPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = new PemReader(new StringReader(privateKey)).readPemObject().getContent();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = new PemReader(new StringReader(publicKey)).readPemObject().getContent();
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}
