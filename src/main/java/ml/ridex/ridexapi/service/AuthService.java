package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationReqDTO;
import ml.ridex.ridexapi.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    public Passenger passengerRegistration(PassengerRegistrationReqDTO data) {
        Passenger newPassenger = new Passenger(data.getPhone(),
                null,
                data.getEmail(),
                data.getName(),
                0,
                0,
                new ArrayList<>(),
                false,
                false);
        try {
            return passengerRepository.insert(newPassenger);
        }
        catch (DuplicateKeyException e) {
            throw new InvalidOperationException("User already exists");
        }

    }
}
