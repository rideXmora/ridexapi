package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public Driver profileComplete(String phone, String name, String email, String city, String driverLicense) {
        Optional<Driver> driverOptional = driverRepository.findByPhone(phone);
        if(driverOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        Driver driver = driverOptional.get();
        if(driver.getSuspend())
            throw new InvalidOperationException("User is suspended");
        driver.setEmail(email);
        driver.setName(name);
        driver.setCity(city);
        driver.setDrivingLicense(driverLicense);
        driver.setEnabled(true);
        return driverRepository.save(driver);
    }
}
