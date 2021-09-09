package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationRequest;
import ml.ridex.ridexapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("passenger/signup")
    public String passengerLogin(@Valid @RequestBody PassengerRegistrationRequest data) {
       try {
           Passenger passenger = authService.passengerRegistration(data);
           System.out.println(passenger);
           return passenger.toString();
       }
       catch (Error e) {
           System.out.println("Error occurred");
           return "Tada";
       }

    }
}
