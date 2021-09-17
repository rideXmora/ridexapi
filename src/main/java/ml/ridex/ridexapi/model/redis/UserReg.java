package ml.ridex.ridexapi.model.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RequiredArgsConstructor
@RedisHash("UserReg")
public class UserReg {
    @Id
    private String phone;
    private String otp;
}
