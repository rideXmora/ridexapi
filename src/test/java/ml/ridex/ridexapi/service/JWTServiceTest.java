package ml.ridex.ridexapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.InvalidJwtAuthenticationException;
import org.bouncycastle.util.io.pem.PemReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {
    JWTService jwtService;

    String privateKeyPath = "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDLEstW2cpg0Wh11DipYKWAv75zNJUKvbmSXZ0eg99cRC8L3vageS08ZygRposPNiINEPP8j+DAB3M0J9jGrcLhM0zeCa1cGJEdV7JsbgYyO6rlczMjoaakrCCvGuSdK0iyLpPyU0NBfQZAfHOe371ADL1MJRQT8TQALYLQMmwr2iVZCfpnXkV2lYKKDDZqqTFlAfsb+Tz3YgfHQwpSfWR6AORWdtXx12OVQp/szCG3lSldSD9H2tSjxhxMEl+G5FFkMxRr1+XuGxC/JGNxJr3V4d2vw/XjXDO1fZ8CHx2GTv1vZy2jlSFiPnW2me3gnHpqkzGEagY4/87wrhNM+gj7AgMBAAECggEBAMQne1KBPQMYQ5bNT44MVNFwZT5S5ERjzwSaeFIpf29YTTliPUf0xMU7v3CNNnWTDIEihwrGz4mtqJIQi0weO8fcDbvMDTCQalu1ghtKi6GqEQFhrVwVoSofOdLqXzmRvb6fTzkbRDPV2Fwh2mz0IZHkvFT429QHgPtK+riwe9w+OQ2mmnesyFd0tx3YmVF2jIqPrG54Wfsn/IxOqd5kR7kNl0EePz0zu0/qch4XUy6ca5675ziqQZReYanwTNBV6C/Eu6JsMefnjIEYLtCHAsBevliI/qjzSbU/kJDz0f+jLhXrcPqdQ93rr+C0Z0Ukj3y8F365LS/Ejp8NGyaCMkECgYEA+FqyYkRisCWHxh3chLAeGq0ujB6PR2Nk7pyHoXZlF4lw8E+3AOK95zpeg2b72C0+iasNNr/7Oq8QAFXf2DwEPDdZnfX0vvazG4UDzWVHO3gSqF0FCD7ZYx1NSmpyZwwuNGWF7IZlGCCTUo2TmiYqmHbIeNvKMEubReMEjGHhO0cCgYEA0VM8GEHAmxcn4c+ayDy6RAnaZK1bLDTE783N/KegtBaN4affHXLLvgO2QrphJ1IMPOnQHmoakddWCEIWOcZiMxwrGuA6fBFIGxvDJ4eYYvzYTendJe1LczmWP14aX6F6s0Lo9WLhbWemvxQLHYAZWWwgTqFf9RQLzQKM60qcNq0CgYEA9oFHV6Qa9E18E1fmXklUyBOnBOr8OdZdAz2YndmfA5qm38+vjBCCf3T/BJv+UxatYQXHytUJLrk+P6b81GrVpVMm/1N2hujk4+bF3GOvLUbD9hrF+OYw7WDTK462WLPNVLKEGD1ZMHZ8486kd7d0Rfp4hwVZDVCuINLw28n3PdcCgYAwdMctilMO095snIVlIwGvbGR899BqPFD2fjrX6On/plTzzsuMYp2df/h6G/EPjCO/GhUOZVt/NUz6HmpMt4rV8AhecVe4jruVYujbRr1U9NkRuDh0ZikYJFWOl7cdOiFQTAXngBtVGzBzfS1Rh+zWUXxtF1hCQIC21YdPlHbzBQKBgAUQM7fogI7D9kk3cQ/LdoHm4K/VkrUphP975gUk7kanahE9bSt3Yz4T6LqywVmiFCwsFofsprynGMIrAXLWMy+YCIfSBynWTG/8FvOz/aUqQBXt3sQhJp442K8TAQOMPFeyZU4rKjvS5or2T59sUgvbEVnBxEcv677AtHPwtloW\n-----END PRIVATE KEY-----";

    String publicKeyPath = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyxLLVtnKYNFoddQ4qWClgL++czSVCr25kl2dHoPfXEQvC972oHktPGcoEaaLDzYiDRDz/I/gwAdzNCfYxq3C4TNM3gmtXBiRHVeybG4GMjuq5XMzI6GmpKwgrxrknStIsi6T8lNDQX0GQHxznt+9QAy9TCUUE/E0AC2C0DJsK9olWQn6Z15FdpWCigw2aqkxZQH7G/k892IHx0MKUn1kegDkVnbV8ddjlUKf7Mwht5UpXUg/R9rUo8YcTBJfhuRRZDMUa9fl7hsQvyRjcSa91eHdr8P141wztX2fAh8dhk79b2cto5UhYj51tpnt4Jx6apMxhGoGOP/O8K4TTPoI+wIDAQAB\n-----END PUBLIC KEY-----";

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JWTService(privateKeyPath, publicKeyPath, 300000);
    }

    @Test
    @DisplayName("createToken should return the token")
    void createTokenShouldReturnToken() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String phone = "+94772597206";
        Role role = Role.PASSENGER;
        String token = jwtService.createToken(phone, role);

        // check claims
        byte[] keyBytes = new PemReader(new StringReader(publicKeyPath)).readPemObject().getContent();

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        JwtParser parser = Jwts.parserBuilder().setSigningKey(kf.generatePublic(spec)).build();
        Jws<Claims> claims = parser.parseClaimsJws(token);
        assertEquals(claims.getBody().getSubject(), phone);
        Calendar calDate = Calendar.getInstance();
        Date expiryBoundary = new Date(calDate.getTimeInMillis() + 240000);
        assertTrue(claims.getBody().getExpiration().after(expiryBoundary));
    }

    @Test
    @DisplayName("resolveToken should extract token from header")
    void resolveTokenShouldExtractToken() {
        // Setup
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTYyMTA2ODIzNywiZXhwIjoxNjIxMDcxODM3fQ.1pz1TOCWMD-E5h-jI5af3-StCrtmeQTkD6X1pWHGpHUhsqS_OvFZxke0yvl7Q4outQyd4EgSAgAKZL5exGGJB9yU03t1aQ-Hfq4IlsifsqyLnqbxRSHb8GaYcT472-D-3glKO4SHYPYikrF3dNiVPtPmJajAObDsbC7Ee_ekE24UXvGn53gkVTfrmhROcAyxcDSFf-wEC1RPFnCbFX5kl_iaoEWTFGCc1CRvHXmvPjeUbbM7ocOOqmBPL4hnEb8jmLAgtNhO6Q3_QMAbXDRYEOQEYZZXXpubL9qGjkIJUUxbJLQRjgDkp8HXSAVwIRIM4agg_RtJoXWD9CRnVCdugQ";
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn(String.format("Bearer %s", token));

        // Evaluate
        String resolvedToken = jwtService.resolveToken(req);
        assertEquals(token, resolvedToken);
    }

    @Test
    @DisplayName("resolve token should return null")
    void resolveTokenShouldReturnNull() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Authorization")).thenReturn("Random Header");

        // Evaluate
        String resolvedToken = jwtService.resolveToken(req);
        assertNull(resolvedToken);
    }

    @Test
    @DisplayName("Validate token should successfully validate token")
    void validateTokenShouldValidate() {
        String phone = "+94771101234";
        Role role = Role.PASSENGER;
        String token = jwtService.createToken(phone, role);
        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Validate token should throw error")
    void validateTokenShouldThrowErr() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sImlhdCI6MTYyMTA2ODIzNywiZXhwIjoxNjIxMDcxODM3fQ.1pz1TOCWMD-E5h-jI5af3-StCrtmeQTkD6X1pWHGpHUhsqS_OvFZxke0yvl7Q4outQyd4EgSAgAKZL5exGGJB9yU03t1aQ-Hfq4IlsifsqyLnqbxRSHb8GaYcT472-D-3glKO4SHYPYikrF3dNiVPtPmJajAObDsbC7Ee_ekE24UXvGn53gkVTfrmhROcAyxcDSFf-wEC1RPFnCbFX5kl_iaoEWTFGCc1CRvHXmvPjeUbbM7ocOOqmBPL4hnEb8jmLAgtNhO6Q3_QMAbXDRYEOQEYZZXXpubL9qGjkIJUUxbJLQRjgDkp8HXSAVwIRIM4agg_RtJoXWD9CRnVCdugQ";
        Exception thrown = assertThrows(InvalidJwtAuthenticationException.class, () -> jwtService.validateToken(token));
        assertEquals(thrown.getMessage(), "Expired or invalid JWT token");
    }

}