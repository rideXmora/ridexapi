package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.PassengerDTO;
import ml.ridex.ridexapi.model.dto.PassengerProfileCompleteDTO;
import ml.ridex.ridexapi.service.PassengerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@PreAuthorize("hasAuthority('PASSENGER')")
@RequestMapping("/api/passenger")
@Tag(name = "Passenger APIs")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/profileComplete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Passenger profile complete/ active the account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile completed"),
            @ApiResponse(responseCode = "400", description = "Database error"),
            @ApiResponse(responseCode = "401", description = "User is suspended")
    })
    public PassengerDTO profileComplete(@Valid @RequestBody PassengerProfileCompleteDTO data, Principal principal) {
        try {
            Passenger passenger = passengerService.profileComplete(principal.getName(), data.getName(), data.getEmail());
            return modelMapper.map(passenger, PassengerDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/profileUpdate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Database error"),
            @ApiResponse(responseCode = "401", description = "User is suspended")
    })
    public PassengerDTO updateProfile(@Valid @RequestBody PassengerProfileCompleteDTO data, Principal principal) {
        try {
            return modelMapper.map(passengerService.updateProfile(
                    principal.getName(),
                    data.getName(),
                    data.getEmail()), PassengerDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
