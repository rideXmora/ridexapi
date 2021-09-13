package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="User Authentication")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/passenger/signup")
    @Operation(summary = "User signup with basic details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Successfully created"),
            @ApiResponse(responseCode = "400",description = "User already exists")
    })
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
    @Operation(summary = "OTP verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP/phone")
    })
    public PassengerVerifiedResDTO passengerVerify(@Valid @RequestBody PassengerVerifyDTO data) {
        try {
            return authService.passengerVerify(data);
        }
        catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
