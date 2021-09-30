package ml.ridex.ridexapi.service;

import com.twilio.exception.ApiException;
import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.CustomHash;
import ml.ridex.ridexapi.helper.Otp;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.User;
import ml.ridex.ridexapi.model.dto.AdminLoginResDTO;
import ml.ridex.ridexapi.model.dto.DriverVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.OrgAdminLoginResDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.redis.UserReg;
import ml.ridex.ridexapi.repository.DriverRepository;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RedisUserRegRepository;
import ml.ridex.ridexapi.repository.OrgAdminRepository;
import ml.ridex.ridexapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RedisUserRegRepository redisUserRegRepository;

    @Autowired
    private OrgAdminRepository orgAdminRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SMSSender smsSender;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpGenerator otpGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${REFRESH_TOKEN_VALIDITY}")
    private long REFRESH_TOKEN_VALIDITY;

    private static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private void redisSaveService(String phone, Role role, String otpHash, long exp) {
        redisUserRegRepository.save(new UserReg(phone, role, otpHash, exp));
    }

    private boolean redisVerifyOtp(String phone, Role role, String otp) {
        Optional<UserReg> userRegOptional = redisUserRegRepository.findById(phone);
        if(userRegOptional.isEmpty())
            throw new EntityNotFoundException("Can't find the user record");
        UserReg userReg = userRegOptional.get();
        if(userReg.getRole() != role) {
            throw new InvalidOperationException("Invalid operation");
        }
        if(userReg.getExp() < Instant.now().getEpochSecond())
            throw new InvalidOperationException("OTP expired");
        CustomHash hash = new CustomHash(otp);
        return hash.verifyHash(userReg.getOtpHash());
    }

    public String sendOTP(String phone, Role role) throws InvalidKeyException {
        Otp otp = otpGenerator.generateOTP();
        CustomHash otpHash = new CustomHash(otp.getOtp());
        this.redisSaveService(phone, role, otpHash.getTxtHash(), otp.getExp());
        try {
            smsSender.sendSms(phone, otp.getOtp());
        } catch (ApiException e) {
            // do nothing
        }
        LOGGER.info(otp.getOtp());
        return "OTP is sent";
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByPhoneAndSuspend(phone, false);
        if(userOptional.isPresent())
            return userOptional.get();
        else
            throw new EntityNotFoundException("User not found");
    }

    public Authentication getAuthentication(String phone) {
        UserDetails userDetails = this.loadUserByUsername(phone);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public User createUser(String phone, String password, List<Role> roles, boolean enable) {
        User user = new User(
                phone,
                passwordEncoder.encode(password),
                roles,
                Instant.now().getEpochSecond() + REFRESH_TOKEN_VALIDITY,
                enable,
                false);
        return userRepository.save(user);
    }

    public PassengerVerifiedResDTO passengerVerification(String phone, String otp) throws DuplicateKeyException {
        User user;
        Passenger passenger;
        if(!this.redisVerifyOtp(phone, Role.PASSENGER, otp))
            throw new InvalidOperationException("Invalid OTP");
        String refreshToken = UUID.randomUUID().toString();
        try {
            user = (User)this.loadUserByUsername(phone);
            Optional<Passenger> passengerOptional = passengerRepository.findByPhone(phone);
            if(passengerOptional.isEmpty())
                throw new InvalidOperationException("Try another phone number"); // must change later
            passenger = passengerOptional.get();
            user.setPassword(passwordEncoder.encode(refreshToken));
            userRepository.save(user);
        } catch (EntityNotFoundException e) {
            user = this.createUser(phone, refreshToken, Arrays.asList(Role.PASSENGER), true);
            passenger = passengerRepository.save(new Passenger(
                    phone,
                    null,
                    null,
                    0,
                    0,
                    new ArrayList<>(),
                    false
            ));
        }
        PassengerVerifiedResDTO response = modelMapper.map(passenger, PassengerVerifiedResDTO.class);
        response.setToken(jwtService.createToken(phone, Arrays.asList(Role.PASSENGER)));
        response.setRefreshToken(refreshToken);
        return response;
    }

    public DriverVerifiedResDTO driverVerification(String phone, String otp) throws DuplicateKeyException {
        User user;
        Driver driver;
        if(!this.redisVerifyOtp(phone, Role.DRIVER, otp))
            throw new InvalidOperationException("Invalid OTP");
        String refreshToken = UUID.randomUUID().toString();
        try {
            user = (User)this.loadUserByUsername(phone);
            Optional<Driver> driverOptional = driverRepository.findByPhone(phone);
            if(driverOptional.isEmpty())
                throw new InvalidOperationException("Try another phone number"); // must change later
            driver = driverOptional.get();
            user.setPassword(passwordEncoder.encode(refreshToken));
            userRepository.save(user);
        } catch (EntityNotFoundException e) {
            user = this.createUser(phone, refreshToken, Arrays.asList(Role.DRIVER), true);
            driver = driverRepository.save(new Driver(
                    phone,
                    null,
                    null,
                    null,
                    null,
                    0,
                    0,
                    0,
                    0,
                    new ArrayList<>(),
                    null,
                    null,
                    DriverStatus.OFFLINE,
                    false));
        }
        DriverVerifiedResDTO response = modelMapper.map(driver, DriverVerifiedResDTO.class);
        response.setToken(jwtService.createToken(phone, Arrays.asList(Role.DRIVER)));
        response.setRefreshToken(refreshToken);
        return response;
    }

    public User adminSignup(String phone, String password) {
        User user = new User(phone, passwordEncoder.encode(password), Arrays.asList(Role.RIDEX_ADMIN), 0, true, false);
        return userRepository.save(user);
    }

    public AdminLoginResDTO adminLogin(String phone) {
        User user = (User)loadUserByUsername(phone);
        if (!user.isEnabled())
            throw new EntityNotFoundException("User is suspended");
        AdminLoginResDTO response = modelMapper.map(user, AdminLoginResDTO.class);
        response.setToken(jwtService.createToken(phone, Arrays.asList(Role.RIDEX_ADMIN)));
        return response;
    }

    public OrgAdmin orgAdminSignup(String name,
                               String email,
                               String password,
                               String phone,
                               String businessRegNo,
                               String basedCity,
                               String address) {

        User user = createUser(phone, password, Arrays.asList(Role.ORG_ADMIN), true);
        OrgAdmin orgAdminData = new OrgAdmin(
                name,
                phone,
                email,
                businessRegNo,
                basedCity,
                address,
                null,
                true);
        return orgAdminRepository.save(orgAdminData);
    }

    public OrgAdminLoginResDTO orgAdminLogin(String phone) {
        User user = (User)loadUserByUsername(phone);
        if (!user.isEnabled())
            throw new EntityNotFoundException("User is suspended");
        OrgAdmin orgAdmin = orgAdminRepository.findByPhone(phone).get();
        OrgAdminLoginResDTO response = modelMapper.map(orgAdmin, OrgAdminLoginResDTO.class);
        response.setToken(jwtService.createToken(phone, Arrays.asList(Role.ORG_ADMIN)));
        return response;
    }

    public User suspend(String phone, Boolean suspend, Role role) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if(userOptional.isEmpty())
            throw new EntityNotFoundException("Invalid phone number");
        User user = userOptional.get();
        if(!user.getRoles().contains(role))
            throw new InvalidOperationException("Do not have permission");
        user.setSuspend(suspend);
        return userRepository.save(user);
    }
}
