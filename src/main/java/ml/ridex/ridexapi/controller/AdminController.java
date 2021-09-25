package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.model.dto.AdminDTO;
import ml.ridex.ridexapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
// @PreAuthorize("hasAuthority('RIDEX_ADMIN')")
@Tag(name = "Admin APIs")
public class AdminController {

    @Autowired
    private UserService userService;

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
}
