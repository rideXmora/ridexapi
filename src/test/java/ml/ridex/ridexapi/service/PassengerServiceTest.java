package ml.ridex.ridexapi.service;

import ml.ridex.ridexapi.enums.RideRequestStatus;
import ml.ridex.ridexapi.exception.EntityNotFoundException;
import ml.ridex.ridexapi.exception.InvalidOperationException;
import ml.ridex.ridexapi.model.dao.Passenger;
import ml.ridex.ridexapi.model.dao.RideRequest;
import ml.ridex.ridexapi.model.daoHelper.Location;
import ml.ridex.ridexapi.model.daoHelper.RideRequestPassenger;
import ml.ridex.ridexapi.repository.PassengerRepository;
import ml.ridex.ridexapi.repository.RideRequestRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    PassengerService passengerService;

    @Mock
    PassengerRepository passengerRepository;
    @Mock
    RideRequestRepository rideRequestRepository;

    Passenger passenger;
    String phone;
    @BeforeEach
    void setUp() {
        passengerService = new PassengerService();
        passenger = new Passenger("94714461798", null, null, 0, 0,true);
        phone = "+94714461798";

        ReflectionTestUtils.setField(passengerService, "passengerRepository", passengerRepository);
        ReflectionTestUtils.setField(passengerService, "rideRequestRepository", rideRequestRepository);
    }

    @Test
    @DisplayName("Profile complete success")
    void profileComplete() {
        when(passengerRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        assertThat(passengerService.profileComplete(phone, "ksr@gmail.com", "ksr")).isNotNull();
    }

    @Test
    @DisplayName("Profile complete fail")
    void profileCompleteUserNotFound() {
        when(passengerRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(null));
        assertThatThrownBy(()-> passengerService.profileComplete(phone, "ksr@gmail.com", "ksr"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Update profile success")
    void updateProfile() {
        when(passengerRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(passenger));
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        assertThat(passengerService.updateProfile(phone, "Raj","ksr@gmail.com")).isNotNull();
    }

    @Test
    @DisplayName("Create ride request")
    void createRideRequest() {
        Location sl = new Location(1.32432,121.12312);
        RideRequestPassenger rideRequestPassenger = new RideRequestPassenger("ids123213", phone, "ksr", 4.3);
        RideRequest rideRequest = new RideRequest(
                rideRequestPassenger,
                sl,
                sl,
                10000,
                RideRequestStatus.PENDING,
                null,
                null,
                Instant.now().getEpochSecond()
        );
        when(passengerRepository.findByPhone(phone)).thenReturn(Optional.ofNullable(passenger));
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(rideRequest);

        assertThat(passengerService.createRideRequest(phone, sl, sl, 10000)).isNotNull();
    }
}