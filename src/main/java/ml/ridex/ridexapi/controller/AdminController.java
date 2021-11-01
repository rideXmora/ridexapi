package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.helper.PasswordGenerator;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dto.*;
import ml.ridex.ridexapi.service.AdminService;
import ml.ridex.ridexapi.service.EmailSender;
import ml.ridex.ridexapi.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
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
    private ModelMapper modelMapper;

    @Autowired
    private EmailSender emailSender;

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

    @GetMapping("/driver/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all drivers")
    public List<DriverDTO> getDriverList() {
        return adminService.getDriverList().stream().map(this::convertToDriverDTO).collect(Collectors.toList());
    }

    @GetMapping("/orgAdmin/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all drivers")
    public List<OrgAdmin> getOrgAdminList() {
        return adminService.getOrgAdminList();
    }

    private DriverDTO convertToDriverDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }

    private PassengerDTO convertToPassengerDTO(Passenger passenger) {
        return modelMapper.map(passenger, PassengerDTO.class);
    }
}
