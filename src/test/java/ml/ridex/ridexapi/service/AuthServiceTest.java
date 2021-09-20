package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
import ml.ridex.ridexapi.model.redis.UserReg;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RedisUserRegRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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
    DriverRepository driverRepository;
    @Mock
    RedisUserRegRepository redisUserRegRepository;
    @Mock
    TwilioSmsSender smsSender;
    @Spy
    private OtpGenerator otpGenerator;
    @Mock
    private JWTService jwtService;

    UserReg userRegPassenger;
    UserReg userRegDriver;
    Passenger passenger;
    Driver driver;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService();
        userRegPassenger = new UserReg("+94714461798", Role.PASSENGER, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", Instant.now().getEpochSecond()+300000);
        userRegDriver = new UserReg("+94714461798", Role.DRIVER, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", Instant.now().getEpochSecond()+300000);

        passenger = new Passenger("+94714461798",null,null,null,0,0,new ArrayList<>(),false,false);
        driver = new Driver("+94714461798", null,null,null,0,0,new ArrayList<>(),null,null,false,false);

        ReflectionTestUtils.setField(authService, "redisUserRegRepository", redisUserRegRepository);
        ReflectionTestUtils.setField(authService, "passengerRepository", passengerRepository);
        ReflectionTestUtils.setField(authService, "driverRepository", driverRepository);
        ReflectionTestUtils.setField(authService, "otpGenerator", otpGenerator);
        ReflectionTestUtils.setField(authService, "jwtService", jwtService);
        ReflectionTestUtils.setField(authService, "smsSender", smsSender);
    }

    @Test
    @DisplayName("Passenger sing up successfully")
    void passengerPhoneAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO("+94714461798");

        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userRegPassenger);
        when(passengerRepository.existsByPhone(userRegPassenger.getPhone())).thenReturn(false);
        doNothing().when(smsSender).sendSms(anyString(), anyString());

        String response = authService.passengerPhoneAuth(phoneAuthDTO);
        assertThat(response).isEqualTo("OTP is sent");
    }

    @Test
    @DisplayName("Passenger OTP verification successfully")
    void passengerVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO("+94714461798", "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegPassenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        Passenger response = authService.passengerVerify(dto);

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Generate JWT")
    public void jwtGen() {
        when(jwtService.createToken(anyString(), any(Role.class))).thenReturn("SDDDDS");

        assertThat(authService.createJwtToken("+94714461798", Role.PASSENGER)).asString();
    }

    @Test
    @DisplayName("Driver sing up successfully")
    void driverPhoneAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO("+94714461798");

        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userRegDriver);
        when(driverRepository.existsByPhone(userRegPassenger.getPhone())).thenReturn(false);
        doNothing().when(smsSender).sendSms(anyString(), anyString());

        String response = authService.driverPhoneAuth(phoneAuthDTO);
        assertThat(response).isEqualTo("OTP is sent");
    }

    @Test
    @DisplayName("Driver signup OTP verification successful")
    public void driverVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO("+94714461798", "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        Driver response = authService.driverVerify(dto);

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Passenger loginAuth send OTP")
    public void passengerLoginAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO("+94714461798");
        when(passengerRepository.existsByPhone(userRegPassenger.getPhone())).thenReturn(true);
        doNothing().when(smsSender).sendSms(anyString(), anyString());
        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userRegPassenger);
        assertThat(authService.passengerLoginAuth(phoneAuthDTO)).isEqualTo("OTP is sent");
    }

    @Test
    @DisplayName("Passenger loginVerify success")
    public void passengerLoginVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO("+94714461798", "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegPassenger));
        when(passengerRepository.findByPhoneAndSuspend(anyString(),anyBoolean())).thenReturn(Optional.ofNullable(passenger));

        Passenger response = authService.passengerLoginVerify(dto);

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Driver loginAuth send OTP")
    public void driverLoginAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO("+94714461798");
        when(driverRepository.existsByPhone(userRegDriver.getPhone())).thenReturn(true);
        doNothing().when(smsSender).sendSms(anyString(), anyString());
        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userRegDriver);
        assertThat(authService.driverLoginAuth(phoneAuthDTO)).isEqualTo("OTP is sent");
    }

    @Test
    @DisplayName("Driver loginVerify success")
    public void driverLoginVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO("+94714461798", "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegDriver));
        when(driverRepository.findByPhoneAndSuspend(anyString(),anyBoolean())).thenReturn(Optional.ofNullable(driver));

        Driver response = authService.driverLoginVerify(dto);

        assertThat(response.getRefreshToken()).asString();
    }
}
