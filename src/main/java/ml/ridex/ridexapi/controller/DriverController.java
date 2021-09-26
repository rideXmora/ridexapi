package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dto.DriverDTO;
import ml.ridex.ridexapi.model.dto.DriverProfileComplete;
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
    public DriverDTO profileComplete(@Valid @RequestBody DriverProfileComplete data, Principal principal) {
        try {
            Driver driver = driverService.profileComplete(principal.getName(), data.getName(), data.getEmail(), data.getCity(), data.getDriverOrganization());
            return modelMapper.map(driver, DriverDTO.class);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
