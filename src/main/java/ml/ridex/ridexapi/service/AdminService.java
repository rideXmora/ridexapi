package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.model.dao.*;
import ml.ridex.ridexapi.model.dto.DriverDTO;
import ml.ridex.ridexapi.model.dto.PassengerDTO;
import ml.ridex.ridexapi.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private OrgAdminRepository orgAdminRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<PassengerDTO> getPassengerList() {
        List<User> passengersAsUsers = userRepository.findByRolesIn(Arrays.asList(Role.PASSENGER));
        List<Passenger> passengers = passengerRepository.findAll();
        List<PassengerDTO> passengerDTOS = passengers.stream().map(this::convertToPassengerDTO).collect(Collectors.toList());
        for(PassengerDTO dto: passengerDTOS) {
            User pUser = passengersAsUsers.stream()
                    .filter(user -> dto.getPhone().equals(user.getPhone())).findAny().orElse(null);
            if(pUser != null) {
                dto.setSuspend(pUser.getSuspend());
            }
        }
        return passengerDTOS;
    }

    public List<DriverDTO> getDriverList() {
        List<User> driversAsUsers = userRepository.findByRolesIn(Arrays.asList(Role.DRIVER));
        List<Driver> drivers = driverRepository.findAll();
        List<DriverDTO> driverDTOS = drivers.stream().map(this::convertToDriverDTO).collect(Collectors.toList());
        for(DriverDTO dto: driverDTOS) {
            User pUser = driversAsUsers.stream()
                    .filter(user -> dto.getPhone().equals(user.getPhone())).findAny().orElse(null);
            if(pUser != null) {
                dto.setSuspend(pUser.getSuspend());
            }
        }
        return driverDTOS;
    }

    public List<OrgAdmin> getOrgAdminList() {
        return orgAdminRepository.findAll();
    }

    private PassengerDTO convertToPassengerDTO(Passenger passenger) {
        return modelMapper.map(passenger, PassengerDTO.class);
    }

    private DriverDTO convertToDriverDTO(Driver driver) {
        return modelMapper.map(driver, DriverDTO.class);
    }
}
