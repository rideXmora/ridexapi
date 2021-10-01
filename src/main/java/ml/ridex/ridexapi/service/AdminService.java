package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.Role;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.User;
import ml.ridex.ridexapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private OrgAdminRepository orgAdminRepository;

    public List<Passenger> getPassengerList() {
        return passengerRepository.findAll();
    }

    public List<Driver> getDriverList() {
        return driverRepository.findAll();
    }

    public List<OrgAdmin> getOrgAdminList() {
        return orgAdminRepository.findAll();
    }

    public User suspendUser(String phone, Boolean suspend, Role role) {
        Optional<User> userOptional = userRepository.findByPhone(phone);
        if(userOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        User user = userOptional.get();
        // Only allowing one end point to delete one type of users
        if(!(user.getRoles().contains(role)))
            throw new InvalidOperationException("Can't suspend user");
        user.setSuspend(suspend);
        return userRepository.save(user);
    }
}
