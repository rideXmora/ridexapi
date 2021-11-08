package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.PasswordGenerator;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.daoHelper.TopPassenger;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.service.AdminService;
import ml.ridex.ridexapi.service.EmailSender;
import ml.ridex.ridexapi.service.PassengerService;
import ml.ridex.ridexapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
// @PreAuthorize("hasAuthority('RIDEX_ADMIN')")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${ADMIN_EMAIL_ADDRESS}")
    private String adminEmailAddress;

    @Value("${EMAIL_ORG_REG_TEMPLATE_ID}")
    private String orgRegEmailTemplate;

    @PostMapping("/register/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Admin registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "User exists")
    })
    public String createAdmin(@Valid @RequestBody AdminDTO data) {
        try {
            userService.adminSignup(data.getPhone(), data.getPassword());
            return "Successfully registered";
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exits");
        }
    }

    @PostMapping("/register/orgAdmin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Org admin creation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Error in server or email server")
    })
    public OrgAdminRegResDTO createOrgAdmin(@Valid @RequestBody OrgAdminRegDTO data) {
        try {
            String password = PasswordGenerator.generateCommonLangPassword();
            OrgAdminRegResDTO response = modelMapper.map(userService.orgAdminSignup(
                    data.getName(),
                    data.getEmail(),
                    password,
                    data.getPhone(),
                    data.getBusinessRegNo(),
                    data.getBasedCity(),
                    data.getAddress())
            , OrgAdminRegResDTO.class);
            Map<String, String> dynamicData = new HashMap<>();
            dynamicData.put("org_name", response.getName());
            dynamicData.put("password", password);
            emailSender.sendHTMLEmail(adminEmailAddress, response.getEmail(), orgRegEmailTemplate, dynamicData);
            return response;
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exits");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred when sending the email");
        }
    }

    @PostMapping("/passenger/suspend")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Un/Suspend passenger")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change suspend status"),
            @ApiResponse(responseCode = "403", description = "Do not have permission")
    })
    public UserDTO suspendPassenger(@Valid @RequestBody SuspendUserDTO data) {
        try {
            return modelMapper.map(userService.suspend(data.getPhone(),
                    data.getSuspend(), Role.PASSENGER), UserDTO.class);
        } catch(InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/driver/suspend")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Suspend Driver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change suspend status"),
            @ApiResponse(responseCode = "403", description = "Do not have permission")
    })
    public UserDTO suspendDriver(@Valid @RequestBody SuspendUserDTO data) {
        try {
            return modelMapper.map(userService.suspend(data.getPhone(), data.getSuspend(), Role.DRIVER), UserDTO.class);
        }catch(InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/orgAdmin/suspend")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Suspend OrgAdmin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "change suspend status"),
            @ApiResponse(responseCode = "403", description = "Do not have permission")
    })
    public UserDTO suspendOrgAdmin(@Valid @RequestBody SuspendUserDTO data) {
        try {
            return modelMapper.map(userService.suspend(data.getPhone(), data.getSuspend(), Role.ORG_ADMIN), UserDTO.class);
        } catch(InvalidOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch(EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/passenger/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all passengers")
    public List<PassengerDTO> getPassengerList() {
        return adminService.getPassengerList();
    }

    @PostMapping("/passenger/pastRides")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all past rides by passenger")
    public List<Ride> getPastRides(@Valid @RequestBody AdminPassengerRidesDTO data) {
        return passengerService.getPastRides(data.getPhone());
    }

    @PostMapping("/passenger/pastRidesStats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get passenger past rides by monthly basis")
    public Map<Month, AdminPassengerRideStatsDTO> getPastRidesStats(@Valid @RequestBody AdminPassengerRidesDTO data) {
        return passengerService.getPastRidesStats(data.getPhone());
    }

    @GetMapping("/passenger/adminRidesStats")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get ride stats in monthly basis")
    public Map<Month, AdminPassengerRideStatsDTO> getAdminRidesStats() {
        return passengerService.getAdminRidesStats();
    }

    @GetMapping("/driver/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all drivers")
    public List<DriverDTO> getDriverList() {
        return adminService.getDriverList();
    }

    @GetMapping("/orgAdmin/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all drivers")
    public List<OrgAdminDTO> getOrgAdminList() {
        return adminService.getOrgAdminList();
    }

    @GetMapping("/totalIncome")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Total income")
    public Double getTotalIncome() {
        return adminService.totalIncome();
    }

    @GetMapping("/passenger/top")
    @ResponseStatus(HttpStatus.OK)
    public List<TopPassenger> getTopDrivers(Principal principal) {
        return  adminService.getTopPassengers();
    }

    @PostMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change password")
    public UserDTO changePassword(@Valid @RequestBody ChangePasswordDTO data, Principal principal) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(principal.getName(), data.getOldPassword()));
            return modelMapper.map(userService.changePassword(principal.getName(), data.getNewPassword()), UserDTO.class);

        } catch (InvalidOperationException | EntityNotFoundException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is suspended");
        }
    }
}
