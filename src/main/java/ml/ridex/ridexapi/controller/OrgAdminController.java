package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.Payment;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.service.OrgAdminService;
import ml.ridex.ridexapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('ORG_ADMIN')")
@RequestMapping("/api/orgAdmin")
@Tag(name = "Org Admin APIs")
public class OrgAdminController {

    @Autowired
    private OrgAdminService orgAdminService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @GetMapping("/drivers/registered")
    @Operation(summary = "Get registered drivers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get list of drivers"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public List<DriverDTO> getRegisterDrivers(Principal principal) {
        try {
            OrgAdmin orgAdmin = orgAdminService.getOrgAdmin(principal.getName());
            return orgAdminService.getRegisteredDrivers(orgAdmin.getId())
                    .stream().map(this::convertToDriverDTO).collect(Collectors.toList());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/drivers/unregistered")
    @Operation(summary = "Get unregistered drivers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get list of drivers"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public List<DriverDTO> getUnregisterDrivers(Principal principal) {
        try {
            OrgAdmin orgAdmin = orgAdminService.getOrgAdmin(principal.getName());
            return orgAdminService.getUnregisteredDrivers(orgAdmin.getId())
                    .stream().map(this::convertToDriverDTO).collect(Collectors.toList());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/drivers/enableDriver")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Accept driver account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully enabled"),
            @ApiResponse(responseCode = "400", description = "Phone number not valid")
    })
    public DriverDTO enableDriver(@Valid @RequestBody OrgAdminEnableDriver data, Principal principal) {
        try {
            OrgAdmin orgAdmin = orgAdminService.getOrgAdmin(principal.getName());
            return this.convertToDriverDTO(orgAdminService.enableDriver(data.getPhone(), orgAdmin.getId(), data.getEnabled()));
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/driver/suspend")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Un/Suspend driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change suspend status"),
            @ApiResponse(responseCode = "403", description = "Do not have permission")
    })
    public UserDTO suspendPassenger(@Valid @RequestBody SuspendUserDTO data) {
        try {
            return modelMapper.map(userService.suspend(data.getPhone(),
                    data.getSuspend(), Role.DRIVER), UserDTO.class);
        } catch(InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/setPayment")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Set payment - need this for cal of ride cost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "payment added/ updated"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public List<OrgAdminPaymentDTO> setPayment(@Valid @RequestBody List<OrgAdminPaymentDTO> data, Principal principal) {
        try {
            return orgAdminService.setPayment(principal.getName(), data)
                    .stream().map(this::convertToOrgAdminPaymentDTO).collect(Collectors.toList());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/ride/past")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get past ride")
    public List<Ride> pastRide(Principal principal) {
        try {
            OrgAdmin orgAdmin = orgAdminService.getOrgAdmin(principal.getName());
            return orgAdminService.getPastRides(orgAdmin.getId());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/ride/past/driver")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get past rides for a driver between to epoch seconds timestamps")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get past rides"),
            @ApiResponse(responseCode = "401", description = "No auth")
    })
    public List<Ride> pastRidesForDriverBetweenTImeInterval(@Valid @RequestBody OrgAdminPastRidesDTO data, Principal principal) {
        try {
            OrgAdmin orgAdmin = orgAdminService.getOrgAdmin(principal.getName());
            return orgAdminService.getPastRidesBetweenTimePeriod(
                    orgAdmin.getId(),
                    data.getPhone(),
                    data.getStartEpoch(),
                    data.getEndEpoch());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private DriverDTO convertToDriverDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }

    private OrgAdminPaymentDTO convertToOrgAdminPaymentDTO(Payment payment) { return modelMapper.map(payment, OrgAdminPaymentDTO.class); }
}
