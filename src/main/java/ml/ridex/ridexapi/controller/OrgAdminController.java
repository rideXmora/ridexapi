package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dto.DriverDTO;
import ml.ridex.ridexapi.model.dto.OrgAdminEnableDriver;
import ml.ridex.ridexapi.service.OrgAdminService;
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

    private DriverDTO convertToDriverDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }
}
