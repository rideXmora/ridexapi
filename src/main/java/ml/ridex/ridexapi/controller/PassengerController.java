package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.CustomStompSessionHandler;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.service.PassengerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
            Passenger passenger = passengerService.profileComplete(principal.getName(), data.getEmail(), data.getName(), data.getNotificationToken());
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

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get profile details")
    public PassengerDTO getPassenger(Principal principal) {
        try{
            return modelMapper.map(passengerService.getPassenger(principal.getName()),PassengerDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/request")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create ride request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request created"),
            @ApiResponse(responseCode = "400", description = "Database error"),
            @ApiResponse(responseCode = "401", description = "User is suspended")
    })
    public RideRequestResDTO createRideRequest(@Valid @RequestBody RideRequestDTO data, Principal principal) {
        try {
            RideRequest rideRequest =passengerService.createRideRequest(
                    principal.getName(),
                    data.getStartLocation(),
                    data.getEndLocation(),
                    data.getDistance());
            passengerService.notifyDrivers(rideRequest);
            return modelMapper.map(rideRequest, RideRequestResDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/ride/request/timeout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Timeout ride request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request timeout"),
            @ApiResponse(responseCode = "400", description = "Invalid rideRequest"),
    })
    public RideRequestResDTO timeoutRideRequest(@Valid @RequestBody PassengerRideRequestTimeoutDTO data, Principal principal) {
        try {
            return modelMapper.map(passengerService.rideRequestTimeout(principal.getName(), data.getId()),
                    RideRequestResDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/getRide")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get ride")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request created"),
            @ApiResponse(responseCode = "400", description = "Database error")
    })
    public CommonRideDTO getRide(@Valid @RequestBody PassengerGetRideDTO data, Principal principal) {
        try {
            return modelMapper.map(passengerService.getRide(principal.getName(), data.getId()), CommonRideDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/confirm")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get ride")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Request created"),
            @ApiResponse(responseCode = "400", description = "Database error")
    })
    public CommonRideDTO confirmRide(@Valid @RequestBody PassengerConfirmRideDTO data, Principal principal) {
        try {
            return modelMapper.map(passengerService.confirmRide(principal.getName(), data.getId(), data.getPassengerFeedback(), data.getDriverRating()), CommonRideDTO.class);
        } catch (EntityNotFoundException | InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/ride/past")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get past ride")
    public List<CommonRideDTO> pastRide(Principal principal) {
        return passengerService.getPastRides(principal.getName()).stream().map(this::convertToCommonRideDTO).collect(Collectors.toList());
    }

    private CommonRideDTO convertToCommonRideDTO(Ride ride) {
        return modelMapper.map(ride, CommonRideDTO.class);
    }
}
