package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.model.redis.DriverState;
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

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get profile details")
    public DriverDTO getPassenger(Principal principal) {
        try {
            return modelMapper.map(driverService.getDriver(principal.getName()),DriverDTO.class);
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

    @PostMapping("/location/update")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update driver location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Try to update location while offline")
    })
    public DriverState updateLocation(@Valid @RequestBody Location location, Principal principal) {
        try {
            return driverService.updateLocation(principal.getName(), location);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/ride/status/arrived")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Driver arrived")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver arrived"),
            @ApiResponse(responseCode = "400", description = "Invalid id or user")
    })
    public Ride rideArrived(@Valid @RequestBody DriverRideStatusChangeDTO data, Principal principal) {
        try {
            return driverService.changeRideStatus(principal.getName(), data.getId(), RideStatus.ARRIVED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/status/picked")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Driver picked the passenger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver picked the passenger"),
            @ApiResponse(responseCode = "400", description = "Invalid id or user")
    })
    public Ride ridePicked(@Valid @RequestBody DriverRideStatusChangeDTO data, Principal principal) {
        try {
            return driverService.changeRideStatus(principal.getName(), data.getId(), RideStatus.PICKED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/status/dropped")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reached to the dropped location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dropped"),
            @ApiResponse(responseCode = "400", description = "Invalid id or user")
    })
    public Ride rideDropped(@Valid @RequestBody DriverRideStatusChangeDTO data, Principal principal) {
        try {
            return driverService.changeRideStatus(principal.getName(), data.getId(), RideStatus.DROPPED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/status/finished")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Finished the ride")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Finished"),
            @ApiResponse(responseCode = "400", description = "Invalid id or user")
    })
    public Ride rideFinished(@Valid @RequestBody DriverRideStatusChangeDTO data, Principal principal) {
        try {
            return driverService.changeRideStatus(principal.getName(), data.getId(), RideStatus.FINISHED);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
