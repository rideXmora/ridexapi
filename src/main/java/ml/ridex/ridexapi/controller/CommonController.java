package ml.ridex.ridexapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.service.CommonService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/common")
@Tag(name = "Common APIs for logged users")
public class CommonController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/allOrg")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all organizations")
    public List<DriverOrganization> getAllOrg() {
        return commonService.getAllOrg().stream().map(this::convertToDriverOrganizationDTO).collect(Collectors.toList());
    }

    private DriverOrganization convertToDriverOrganizationDTO(OrgAdmin orgAdmin) {
        return modelMapper.map(orgAdmin, DriverOrganization.class);
    }
}
