package ml.ridex.ridexapi.controller;

import com.mongodb.MongoWriteException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerDTO;
import ml.ridex.ridexapi.model.dto.PassengerRegistrationReqDTO;
import ml.ridex.ridexapi.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("passenger/signup")
    public PassengerDTO passengerLogin(@Valid @RequestBody PassengerRegistrationReqDTO data) {
       try {
           Passenger passenger = authService.passengerRegistration(data);
           return modelMapper.map(passenger, PassengerDTO.class);
       }
       catch (InvalidOperationException e) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
       }
    }
}
