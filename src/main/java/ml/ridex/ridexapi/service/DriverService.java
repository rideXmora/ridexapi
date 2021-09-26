package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.daoHelper.DriverOrganization;
import ml.ridex.ridexapi.model.daoHelper.Vehicle;
import ml.ridex.ridexapi.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {
    @Autowired
    private DriverRepository driverRepository;

    public Driver getDriver(String phone) {
        Optional<Driver> driverOptional = driverRepository.findByPhone(phone);
        if(driverOptional.isEmpty())
            throw new EntityNotFoundException("User not found");
        return driverOptional.get();
    }

    public Driver profileComplete(
            String phone,
            String name,
            String email,
            String city,
            DriverOrganization driverOrganization) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        if(driver.getSuspend())
            throw new InvalidOperationException("User is suspended");
        driver.setEmail(email);
        driver.setName(name);
        driver.setCity(city);
        driver.setDriverOrganization(driverOrganization);
        driver.setEnabled(true);
        return driverRepository.save(driver);
    }

    public Driver addVehicle(String phone, Vehicle vehicle) throws EntityNotFoundException {
        Driver driver = getDriver(phone);
        if(driver.getSuspend())
            throw new InvalidOperationException("User is suspended");
        driver.setVehicle(vehicle);
        return driverRepository.save(driver);
    }
}
