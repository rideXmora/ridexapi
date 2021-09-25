package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dto.DriverVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PassengerVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
import ml.ridex.ridexapi.model.dto.AdminDTO;
import ml.ridex.ridexapi.model.dto.AdminLoginResDTO;
import ml.ridex.ridexapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/passenger/phoneAuth")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Send OTP/passenger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved user in memory"),
            @ApiResponse(responseCode = "500", description = "Unable to generate OTP")
    })
    public String passengerPhoneAuth(@Valid @RequestBody PhoneAuthDTO data) {
        try {
            return userService.sendOTP(data.getPhone(), Role.PASSENGER);
        } catch (InvalidKeyException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/passenger/phoneVerify")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify OTP/passenger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login/Signup"),
            @ApiResponse(responseCode = "401", description = "Invalid OTP/phone")
    })
    public PassengerVerifiedResDTO passengerPhoneVerify(@Valid @RequestBody OtpVerifyDTO data) {
        try{
            PassengerVerifiedResDTO response = userService.passengerVerification(data.getPhone(), data.getOtp());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getPhone(),response.getRefreshToken()));
            return response;
        }
        catch (InvalidOperationException | EntityNotFoundException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/driver/phoneVerify")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify OTP/driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login/Signup"),
            @ApiResponse(responseCode = "401", description = "Invalid OTP/phone")
    })
    public DriverVerifiedResDTO driverPhoneVerify(@Valid @RequestBody OtpVerifyDTO data) {
        try{
            DriverVerifiedResDTO response = userService.driverVerification(data.getPhone(), data.getOtp());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getPhone(),response.getRefreshToken()));
            return response;
        }
        catch (InvalidOperationException | EntityNotFoundException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/admin/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Overall admin login")
    public AdminLoginResDTO adminLogin(@Valid @RequestBody AdminDTO data) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getPhone(), data.getPassword()));
            return userService.adminLogin(data.getPhone());

        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
