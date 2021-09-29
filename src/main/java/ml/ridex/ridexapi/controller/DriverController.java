package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import ml.ridex.ridexapi.model.dto.DriverDTO;
import ml.ridex.ridexapi.model.dto.DriverProfileCompleteDTO;
import ml.ridex.ridexapi.model.dto.DriverProfileUpdateDTO;
import ml.ridex.ridexapi.model.dto.RideRequestAcceptDTO;
import ml.ridex.ridexapi.model.dto.DriverLocationDTO;
import ml.ridex.ridexapi.service.DriverService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@PreAuthorize("hasAuthority('DRIVER')")
@RequestMapping("/api/driver")
@Tag(name = "Driver APIs")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/profileComplete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Driver profile complete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully completed"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "User is suspended")
    })
    public DriverDTO profileComplete(@Valid @RequestBody DriverProfileCompleteDTO data, Principal principal) {
        try {
            Driver driver = driverService.profileComplete(principal.getName(), data.getName(), data.getEmail(), data.getCity(), data.getDriverOrganization());
            return modelMapper.map(driver, DriverDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/addVehicle")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully completed"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public DriverDTO addVehicle(@Valid @RequestBody Vehicle data, Principal principal) {
        try {
           Driver driver = driverService.addVehicle(principal.getName(), data);
           return modelMapper.map(driver, DriverDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/profileUpdate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Driver profile update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public DriverDTO profileUpdate(@Valid @RequestBody DriverProfileUpdateDTO data, Principal principal) {
        try {
            Driver driver = driverService.profileUpdate(principal.getName(), data.getCity());
            return modelMapper.map(driver, DriverDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/ride/accept")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Accept ride request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accepted"),
            @ApiResponse(responseCode = "400", description = "User not found"),
    })
    public Ride acceptRideRequest(@Valid @RequestBody RideRequestAcceptDTO data, Principal principal) {
        try {
            Ride ride = driverService.acceptRideRequest(principal.getName(), data.getId());
            return ride;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/toggleStatus")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change driver state between ONLINE, OFFLINE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Change the status"),
            @ApiResponse(responseCode = "400", description = "User not found"),
    })
    public DriverDTO toggleStatus(@Valid @RequestBody DriverLocationDTO data, Principal principal) {
        try {
            Driver driver = driverService.toggleStatus(principal.getName(), data.getLocation());
            return modelMapper.map(driver, DriverDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
