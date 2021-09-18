package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.helper.Otp;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
import ml.ridex.ridexapi.model.redis.UserReg;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RedisUserRegRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    AuthService authService;
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    RedisUserRegRepository redisUserRegRepository;
    @Mock
    TwilioSmsSender smsSender;
    @Mock
    private OtpGenerator otpGenerator;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private JWTService jwtService;

    UserReg userReg;
    Passenger passenger;

    @BeforeEach
    void setUp() {
        authService = new AuthService();
        userReg = new UserReg("+94714461798", Role.PASSENGER, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", Instant.now().getEpochSecond()+300000);
        passenger = new Passenger("+94714461798",null,null,null,0,0,new ArrayList<>(),false,false);
        ReflectionTestUtils.setField(authService, "redisUserRegRepository", redisUserRegRepository);
        ReflectionTestUtils.setField(authService, "passengerRepository", passengerRepository);
        ReflectionTestUtils.setField(authService, "otpGenerator", otpGenerator);
        ReflectionTestUtils.setField(authService, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(authService, "jwtService", jwtService);
        ReflectionTestUtils.setField(authService, "smsSender", smsSender);
    }

    @Test
    void passengerPhoneAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO("+94714461798");

        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userReg);
        when(passengerRepository.existsByPhone(userReg.getPhone())).thenReturn(false);
        when(otpGenerator.generateOTP()).thenReturn(new Otp("1234", 1000));
        doNothing().when(smsSender).sendSms(anyString(), anyString());

        String response = authService.passengerPhoneAuth(phoneAuthDTO);
        assertThat(response).isEqualTo("OTP is sent");
    }

    @Test
    void passengerVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO("+94714461798", "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userReg));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
        when(jwtService.createToken(anyString(), any(Role.class))).thenReturn("SDDDDS");
        when(modelMapper.map(any(Passenger.class), any())).thenReturn(new PassengerVerifiedResDTO());
        PassengerVerifiedResDTO response = authService.passengerVerify(dto);

        assertThat(response.getToken()).asString();
    }
}
