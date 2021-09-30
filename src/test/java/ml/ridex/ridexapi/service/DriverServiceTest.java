package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.DriverStatus;
import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.enums.RideStatus;
import ml.ridex.ridexapi.enums.VehicleType;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.model.dao.Driver;
import ml.ridex.ridexapi.model.dao.OrgAdmin;
import ml.ridex.ridexapi.model.dao.Ride;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.*;
import ml.ridex.ridexapi.model.redis.DriverState;
import ml.ridex.ridexapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    DriverService driverService;
    @Mock
    DriverRepository driverRepository;
    @Mock
    RideRepository rideRepository;
    @Mock
    RideRequestRepository rideRequestRepository;
    @Mock
    OrgAdminRepository orgAdminRepository;
    @Mock
    RedisDriverStateRepository redisDriverStateRepository;

    Driver driver;
    String phone;

    @BeforeEach
    void setup() {
        driverService = new DriverService();
        driver = new Driver("94714461798", null, null,null ,null,0,0, 0,0, new ArrayList<>(), null, null, DriverStatus.OFFLINE,false);
        phone = "+94714461798";

        ReflectionTestUtils.setField(driverService, "driverRepository", driverRepository);
        ReflectionTestUtils.setField(driverService, "rideRepository", rideRepository);
        ReflectionTestUtils.setField(driverService, "rideRequestRepository", rideRequestRepository);
        ReflectionTestUtils.setField(driverService, "redisDriverStateRepository", redisDriverStateRepository);
        ReflectionTestUtils.setField(driverService, "orgAdminRepository", orgAdminRepository);
    }

    @Test
    @DisplayName("Get driver")
    void getDriver() {
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        assertThat(driverService.getDriver(phone)).isNotNull();
    }

    @Test
    @DisplayName("Get driver throw error")
    void getDriverThrowError() {
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(null));
        assertThatThrownBy(() -> driverService.getDriver(phone)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Profile complete")
    void profileComplete() {
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertThat(driverService.profileComplete(phone, "ksr", null,null,null)).isNotNull();
    }

    @Test
    @DisplayName("Add vehicle")
    void addVehicle() {
        Vehicle vehicle = new Vehicle("NW1231", VehicleType.CAR,"Alto","dsfsd","ins", "124");
        driver.setVehicle(vehicle);
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertThat(driverService.addVehicle(phone, vehicle).getVehicle().getModel()).isEqualTo("Alto");
    }

    @Test
    @DisplayName("Update profile")
    void profileUpdate() {
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        assertThat(driverService.profileUpdate(phone, "Kuru")).isNotNull();
    }

    @Test
    void acceptRideRequest() {
        OrgAdmin orgAdmin = new OrgAdmin("ksr", phone,"ksr@gmail.com", "sadafd","Col","Address",null,true);
        Vehicle vehicle = new Vehicle("NW1231", VehicleType.CAR,"Alto","dsfsd","ins", "124");

        Payment payment = new Payment();
        payment.setRatePerMeter(1000.0);
        payment.setRateWaitingPerMin(10.1);
        payment.setDiscount(12.0);

        orgAdmin.setPayment(payment);

        driver.setVehicle(vehicle);
        driver.setEnabled(true);
        driver.setDriverOrganization(new DriverOrganization("OrgId", "Uber"));
        Location sl = new Location(1.32432,121.12312);
        RideRequest rideRequest = new RideRequest(
                new RideRequestPassenger("id",phone,"ksr",12.2),
                sl,
                sl,
                10000,
                RideRequestStatus.PENDING,
                new RideRequestDriver("idDr",phone, new RideRequestVehicle("Nw",VehicleType.CAR,"Nixxan"),12.78),
                new DriverOrganization("id3","nameOrg"),
                Instant.now().getEpochSecond());
        Ride ride = new Ride(rideRequest, "","", 1222.4, RideStatus.ACCEPTED);
        when(rideRequestRepository.findById(anyString())).thenReturn(Optional.of(rideRequest));
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(rideRequest);
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        when(orgAdminRepository.findById(anyString())).thenReturn(Optional.ofNullable(orgAdmin));

        assertThat(driverService.acceptRideRequest(phone, "id1").getRideRequest()).isNotNull();
    }

    @Test
    @DisplayName("Toggle driver Status to ONLINE")
    void toggleStatus() {
        Location location = new Location(2.111,54.0);
        DriverState driverState = new DriverState(phone, location, Instant.now().getEpochSecond());
        driver.setDriverStatus(DriverStatus.OFFLINE);
        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);
        when(redisDriverStateRepository.save(any(DriverState.class))).thenReturn(driverState);

        assertThat(driverService.toggleStatus(phone, location).getDriverStatus()).isEqualTo(DriverStatus.ONLINE);
    }

    @Test
    @DisplayName("Updating driver location when available in redis")
    void updateLocation() {
        Location location = new Location(2.111,54.0);
        DriverState driverState = new DriverState(phone, location, Instant.now().getEpochSecond());

        when(redisDriverStateRepository.findById(phone)).thenReturn(Optional.ofNullable(driverState));
        when(redisDriverStateRepository.save(driverState)).thenReturn(driverState);

        assertThat(driverService.updateLocation(phone,location)).isNotNull();
    }

    @Test
    @DisplayName("Updating driver location when not available in redis")
    void updateLocationNewObject() {
        driver.setDriverStatus(DriverStatus.ONLINE);
        Location location = new Location(2.111,54.0);
        DriverState driverState = new DriverState(phone, location, Instant.now().getEpochSecond());

        when(driverRepository.findByPhone(anyString())).thenReturn(Optional.ofNullable(driver));
        when(redisDriverStateRepository.findById(phone)).thenReturn(Optional.ofNullable(null));
        when(redisDriverStateRepository.save(driverState)).thenReturn(driverState);

        assertThat(driverService.updateLocation(phone,location)).isNotNull();
    }
}