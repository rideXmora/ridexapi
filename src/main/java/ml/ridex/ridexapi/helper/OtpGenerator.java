package ml.ridex.ridexapi.helper;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import javax.crypto.KeyGenerator;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class OtpGenerator {
    private final TimeBasedOneTimePasswordGenerator totp;
    private final KeyGenerator keyGenerator;
    private final Key key;

    public OtpGenerator() throws NoSuchAlgorithmException {
        this.totp = new TimeBasedOneTimePasswordGenerator();
        this.keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
        this.keyGenerator.init(160);
        this.key = keyGenerator.generateKey();
    }

    public String generateOTP() throws InvalidKeyException {
        final Instant now = Instant.now();
        return totp.generateOneTimePasswordString(key, now);
    }
}
