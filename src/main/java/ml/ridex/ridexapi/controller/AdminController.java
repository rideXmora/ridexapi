package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.helper.PasswordGenerator;
import ml.ridex.ridexapi.model.dto.AdminDTO;
import ml.ridex.ridexapi.model.dto.OrgAdminRegDTO;
import ml.ridex.ridexapi.model.dto.OrgAdminRegResDTO;
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
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
// @PreAuthorize("hasAuthority('RIDEX_ADMIN')")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

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
}
