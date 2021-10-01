package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.User;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.model.redis.UserReg;
import ml.ridex.ridexapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PassengerRepository passengerRepository;
    @Mock
    DriverRepository driverRepository;
    @Mock
    OrgAdminRepository orgAdminRepository;
    @Mock
    RedisUserRegRepository redisUserRegRepository;
    @Mock
    SMSSender smsSender;
    @Mock
    EmailSender emailSender;
    @Spy
    OtpGenerator otpGenerator;
    @Mock
    JWTService jwtService;
    @Spy
    PasswordEncoder passwordEncoder;
    @Spy
    ModelMapper modelMapper;

    User userPassenger;
    User userDriver;
    Passenger passenger;
    Driver driver;
    UserReg userRegPassenger;
    UserReg userRegDriver;
    String phone;

    @BeforeEach
    void setup() {
        userService = new UserService();
        userPassenger = new User("+94714461798", "password", Arrays.asList(Role.PASSENGER), Instant.now().getEpochSecond()+100000, true, false);
        userDriver = new User("+94714461798", "password", Arrays.asList(Role.DRIVER), Instant.now().getEpochSecond()+100000, true, false);
        passenger = new Passenger("94714461798", null, null, 0, 0,true);
        driver = new Driver("94714461798", null, null,null ,null,0,0, 0,0, null, null, DriverStatus.OFFLINE,false);
        userRegPassenger = new UserReg("+94714461798", Role.PASSENGER, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", Instant.now().getEpochSecond()+300000);
        userRegDriver = new UserReg("+94714461798", Role.DRIVER, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92", Instant.now().getEpochSecond()+300000);
        phone = "+94714461798";

        ReflectionTestUtils.setField(userService, "redisUserRegRepository", redisUserRegRepository);
        ReflectionTestUtils.setField(userService, "passengerRepository", passengerRepository);
        ReflectionTestUtils.setField(userService, "driverRepository", driverRepository);
        ReflectionTestUtils.setField(userService, "orgAdminRepository", orgAdminRepository);
        ReflectionTestUtils.setField(userService, "userRepository", userRepository);
        ReflectionTestUtils.setField(userService, "otpGenerator", otpGenerator);
        ReflectionTestUtils.setField(userService, "jwtService", jwtService);
        ReflectionTestUtils.setField(userService, "smsSender", smsSender);
        ReflectionTestUtils.setField(userService, "emailSender", emailSender);
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "modelMapper", modelMapper);
    }

    @Test
    @DisplayName("Passenger sing up successfully")
    void phoneAuth() throws InvalidKeyException {
        PhoneAuthDTO phoneAuthDTO = new PhoneAuthDTO(phone);

        when(redisUserRegRepository.save(any(UserReg.class))).thenReturn(userRegPassenger);
        doNothing().when(smsSender).sendSms(anyString(), anyString());

        String response = userService.sendOTP(phoneAuthDTO.getPhone(),Role.PASSENGER);
        assertThat(response).isEqualTo("OTP is sent");
    }

    @Test
    @DisplayName("Passenger OTP verification successfully/with reg")
    void passengerVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO(phone, "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegPassenger));
        when(userRepository.findByPhoneAndSuspend(dto.getPhone(),false)).thenReturn(Optional.ofNullable(userPassenger));
        when(passengerRepository.findByPhone(dto.getPhone())).thenReturn(Optional.ofNullable(passenger));
        when(userRepository.save(any(User.class))).thenReturn(userPassenger);

        PassengerVerifiedResDTO response = userService.passengerVerification(dto.getPhone(), dto.getOtp());

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Passenger OTP verification successfully")
    void passengerSignupVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO(phone, "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegPassenger));
        when(userRepository.findByPhoneAndSuspend(dto.getPhone(),false)).thenReturn(Optional.ofNullable(null));
        when(userRepository.save(any(User.class))).thenReturn(userPassenger);
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        PassengerVerifiedResDTO response = userService.passengerVerification(dto.getPhone(), dto.getOtp());

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Driver OTP verification successfully/with reg")
    void driverVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO(phone, "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegDriver));
        when(userRepository.findByPhoneAndSuspend(dto.getPhone(),false)).thenReturn(Optional.ofNullable(userDriver));
        when(driverRepository.findByPhone(dto.getPhone())).thenReturn(Optional.ofNullable(driver));
        when(userRepository.save(any(User.class))).thenReturn(userDriver);

        DriverVerifiedResDTO response = userService.driverVerification(dto.getPhone(), dto.getOtp());

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Passenger OTP verification successfully")
    void driverSignupVerify() {
        OtpVerifyDTO dto = new OtpVerifyDTO(phone, "123456");
        when(redisUserRegRepository.findById(anyString())).thenReturn(Optional.ofNullable(userRegDriver));
        when(userRepository.findByPhoneAndSuspend(dto.getPhone(),false)).thenReturn(Optional.ofNullable(null));
        when(userRepository.save(any(User.class))).thenReturn(userDriver);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverVerifiedResDTO response = userService.driverVerification(dto.getPhone(), dto.getOtp());

        assertThat(response.getRefreshToken()).asString();
    }

    @Test
    @DisplayName("Return Authentication object")
    void getAuthentication() {
        when(userRepository.findByPhoneAndSuspend(phone,false)).thenReturn(Optional.ofNullable(userPassenger));

        assertThat(userService.getAuthentication(phone)).isNotNull();
    }

    @Test
    @DisplayName("Admin signup")
    void adminSignup() {
        String password = "password";
        when(userRepository.save(any(User.class))).thenReturn(userPassenger);

        assertThat(userService.adminSignup(phone, password).getPassword()).asString();
    }

    @Test
    @DisplayName("Admin login")
    void adminLogin() {
        when(userRepository.findByPhoneAndSuspend(phone,false)).thenReturn(Optional.ofNullable(userDriver));
        when(jwtService.createToken(anyString(), anyList())).thenReturn("Token");
        AdminLoginResDTO data = userService.adminLogin(phone);
        assertThat(data.getToken()).asString();
    }

    @Test
    @DisplayName("OrgAdmin signup/success")
    void orgAdminSignup() {
        OrgAdmin orgAdmin = new OrgAdmin("ksr", phone, "ksr@gmail.com", "SF232","Kurunegala", "Adress",null, true);
        when(orgAdminRepository.save(any(OrgAdmin.class))).thenReturn(orgAdmin);

        assertThat(userService.orgAdminSignup("ksr", "ksr@gmail.com","password","94714461798", "SF232","Kurunegala", "Adress").getEnabled()).isTrue();
    }

    @Test
    @DisplayName("OrgAdmin login")
    void orgAdminLogin() {
        OrgAdmin orgAdmin = new OrgAdmin("ksr", "94714461798", "ksr@gmail.com", "SF232","Kurunegala", "Adress",null, true);
        when(userRepository.findByPhoneAndSuspend(phone,false)).thenReturn(Optional.ofNullable(userDriver));
        when(jwtService.createToken(anyString(), anyList())).thenReturn("Token");
        when(orgAdminRepository.findByPhone(anyString())).thenReturn(Optional.of(orgAdmin));

        OrgAdminLoginResDTO res = userService.orgAdminLogin(phone);

        assertThat(res.getToken()).isEqualTo("Token");
    }

    @Test
    @DisplayName("Suspend user")
    void suspend() {
        when(userRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(userDriver));
        when(userRepository.save(any(User.class))).thenReturn(userDriver);
        userDriver.setSuspend(true);
        assertThat(userService.suspend(phone, true, Role.DRIVER).getSuspend()).isTrue();
    }

    @Test
    @DisplayName("Suspend user forbidden")
    void suspendForbidden() {
        when(userRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(userDriver));
        userDriver.setSuspend(true);
        assertThatThrownBy(()-> userService.suspend(phone, true, Role.PASSENGER))
                .isInstanceOf(InvalidOperationException.class);
    }
}
