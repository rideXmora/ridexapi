package ml.ridex.ridexapi.controller;

import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dto.PassengerDTO;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationReqDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifyDTO;
import ml.ridex.ridexapi.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/passenger/signup")
    public PassengerDTO passengerLogin(@Valid @RequestBody PassengerRegistrationReqDTO data) {
       try {
          return authService.passengerRegistration(data);
       }
       catch (InvalidOperationException e) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
       }
       catch (InvalidKeyException e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
       }
    }

    @PostMapping("/passenger/verify")
    public PassengerVerifiedResDTO passengerVerify(@Valid @RequestBody PassengerVerifyDTO data) {
        try {
            return authService.passengerVerify(data);
        }
        catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
