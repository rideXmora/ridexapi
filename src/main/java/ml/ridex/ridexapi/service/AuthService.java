package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationRequest;
import ml.ridex.ridexapi.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    public Passenger passengerRegistration(PassengerRegistrationRequest data) {
        Passenger newPassenger = new Passenger(data.getPhone(), null, data.getEmail(), data.getName(), 0, 0, new ArrayList<>(), false);
        Passenger passenger = passengerRepository.insert(newPassenger);
        return passenger;
    }
}
