package ml.ridex.ridexapi.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OtpGeneratorTest {
    private OtpGenerator otpGenerator;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        otpGenerator = new OtpGenerator();
    }

    @Test
    public void getOtp() throws InvalidKeyException {
        String otp = otpGenerator.generateOTP();
        assertThat(otp).asString();
    }
}
