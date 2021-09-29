package ml.ridex.ridexapi.model.redis;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ml.ridex.ridexapi.model.daoHelper.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RequiredArgsConstructor
@RedisHash("DriverStatus")
public class DriverState {
    @Id
    private String phone;
    private Location location;
    private long lastSeen;

    public DriverState(String phone, Location location, long lastSeen) {
        this.phone = phone;
        this.location = location;
        this.lastSeen = lastSeen;
    }
}
