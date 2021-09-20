package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.CustomHash;
import ml.ridex.ridexapi.helper.Otp;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
import ml.ridex.ridexapi.model.redis.UserReg;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RedisUserRegRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private RedisUserRegRepository redisUserRegRepository;

    @Autowired
    private TwilioSmsSender smsSender;

    @Autowired
    private OtpGenerator otpGenerator;

    @Autowired
    private JWTService jwtService;

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

    public String passengerPhoneAuth(PhoneAuthDTO phoneAuthDTO) throws InvalidKeyException {
        if(passengerRepository.existsByPhone(phoneAuthDTO.getPhone()))
            throw new InvalidOperationException("Passenger already exists");
        Otp otp = otpGenerator.generateOTP();
        CustomHash otpHash = new CustomHash(otp.getOtp());
        this.redisSaveService(phoneAuthDTO.getPhone(), Role.PASSENGER, otpHash.getTxtHash(), otp.getExp());
        smsSender.sendSms(phoneAuthDTO.getPhone(), otp.getOtp());
        return "OTP is sent";
    }

    public Passenger passengerVerify(OtpVerifyDTO data) {
        if(!this.redisVerifyOtp(data.getPhone(), Role.PASSENGER, data.getOtp()))
            throw new InvalidOperationException("Invalid OTP");

        CustomHash uuidHashGen = new CustomHash();
        Passenger passenger = new Passenger(
                data.getPhone(),
                uuidHashGen.getTxtHash(),
                null,
                null,
                0,
                0,
                new ArrayList<>(),
                false,
                false);
        try {
            passengerRepository.save(passenger);
            passenger.setRefreshToken(uuidHashGen.getTxt());
            return passenger;
        }
        catch (DuplicateKeyException e) {
            throw new InvalidOperationException("User already exists");
        }
    }

    public String createJwtToken(String phone, Role role) {
        return  jwtService.createToken(phone, role);
    }
}
