package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
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

    @PostMapping("/passenger/phoneAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send OTP")
    @ApiResponse(responseCode = "201", description = "Saved user in memory")
    public String phoneAuth(@Valid @RequestBody PhoneAuthDTO phoneAuthDTO) {
        try {
            return authService.passengerPhoneAuth(phoneAuthDTO);
        } catch (InvalidKeyException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
         catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
         }
    }

    @PostMapping("/passenger/verify")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "OTP verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Invalid OTP/phone")
    })
    public PassengerVerifiedResDTO passengerVerify(@Valid @RequestBody OtpVerifyDTO data) {
        try {
            return authService.passengerVerify(data);
        }
        catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
