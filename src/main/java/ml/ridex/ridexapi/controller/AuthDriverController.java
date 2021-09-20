package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dto.DriverVerifiedResDTO;
import ml.ridex.ridexapi.model.dto.OtpVerifyDTO;
import ml.ridex.ridexapi.model.dto.PhoneAuthDTO;
import ml.ridex.ridexapi.service.AuthService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/api/auth/driver")
@Tag(name="Driver Authentication")
public class AuthDriverController {
        @Autowired
        private AuthService authService;

        @Autowired
        private ModelMapper modelMapper;

        @PostMapping("/phoneAuth")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Driver Send OTP")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Saved user in memory"),
                @ApiResponse(responseCode = "400", description = "Driver already exits")
        })
        public String driverPhoneAuth(@Valid @RequestBody PhoneAuthDTO phoneAuthDTO) {
                try {
                        return authService.driverPhoneAuth(phoneAuthDTO);
                } catch (InvalidKeyException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                } catch (InvalidOperationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
        }

        @PostMapping("/verify")
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "Driver OTP verification")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Successful"),
                @ApiResponse(responseCode = "400", description = "Invalid OTP/phone")
        })
        public DriverVerifiedResDTO driverVerify(@Valid @RequestBody OtpVerifyDTO data) {
                try {
                        Driver driver = authService.driverVerify(data);
                        String token = authService.createJwtToken(data.getPhone(), Role.DRIVER);
                        DriverVerifiedResDTO responseDTO = modelMapper.map(driver, DriverVerifiedResDTO.class);
                        responseDTO.setToken(token);
                        return responseDTO;
                }
                catch (InvalidOperationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
                catch (EntityNotFoundException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }
        }

        @PostMapping("/loginAuth")
        @ResponseStatus(HttpStatus.OK)
        @Operation(summary = "Driver/login Send OTP")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Saved user in memory"),
                @ApiResponse(responseCode = "400", description = "Driver already exits")
        })
        public String driverLoginAuth(@Valid @RequestBody PhoneAuthDTO phoneAuthDTO) {
                try {
                        return authService.driverLoginAuth(phoneAuthDTO);
                } catch(InvalidOperationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (InvalidKeyException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
                }
        }

        @PostMapping("/loginVerify")
        @ResponseStatus(HttpStatus.CREATED)
        @Operation(summary = "OTP verification")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Login successful"),
                @ApiResponse(responseCode = "400", description = "Invalid OTP/phone")
        })
        public DriverVerifiedResDTO driverLoginVerify(@Valid @RequestBody OtpVerifyDTO data) {
                try {
                        Driver driver = authService.driverLoginVerify(data);
                        String token = authService.createJwtToken(data.getPhone(), Role.DRIVER);
                        DriverVerifiedResDTO responseDTO = modelMapper.map(driver, DriverVerifiedResDTO.class);
                        responseDTO.setToken(token);
                        return responseDTO;
                } catch(InvalidOperationException e) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
        }
}
