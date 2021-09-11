package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.CustomHash;
import ml.ridex.ridexapi.helper.OtpGenerator;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerDTO;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationReqDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifyDTO;
import ml.ridex.ridexapi.repository.PassengerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.util.ArrayList;

@Service
public class AuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TwilioSmsSender smsSender;

    @Autowired
    private OtpGenerator otpGenerator;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JWTService jwtService;

    public PassengerDTO passengerRegistration(PassengerRegistrationReqDTO data) throws InvalidKeyException {
        String otp = otpGenerator.generateOTP();
        Passenger newPassenger = new Passenger(data.getPhone(),
                null,
                data.getEmail(),
                data.getName(),
                0,
                0,
                new ArrayList<>(),
                false,
                false,
                otp);
        try {
            smsSender.sendSms(data.getPhone(), otp);
            Passenger passenger = passengerRepository.insert(newPassenger);
            return modelMapper.map(passenger, PassengerDTO.class);
        }
        catch (DuplicateKeyException e) {
            throw new InvalidOperationException("User already exists");
        }
    }

    public PassengerVerifiedResDTO passengerVerify(PassengerVerifyDTO data) {
        Passenger passenger = passengerRepository.findByPhoneAndOtp(data.getPhone(), data.getOtp());
        if(passenger == null) throw new InvalidOperationException("Invalid data");

        passenger.setOtp(""); // Remove otp
        CustomHash uuidHashGen = new CustomHash();
        passenger.setToken(uuidHashGen.getUuidHash());
        passenger.setEnabled(true); // Enable passenger
        passengerRepository.save(passenger);
        // Create JWT token
        String token = jwtService.createToken(passenger.getPhone(), Role.PASSENGER);

        PassengerVerifiedResDTO responseDTO = modelMapper.map(passenger, PassengerVerifiedResDTO.class);
        responseDTO.setToken(token);
        responseDTO.setRefreshToken(uuidHashGen.getUuid());

        return responseDTO;
    }
}
