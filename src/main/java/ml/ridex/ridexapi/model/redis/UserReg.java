package ml.ridex.ridexapi.model.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ml.ridex.ridexapi.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RequiredArgsConstructor
@RedisHash("UserReg")
public class UserReg {
    @Id
    private String phone;
    private Role role;
    private String otpHash;
    private long exp;

    public UserReg(String phone, Role role, String otp, long exp) {
        this.phone = phone;
        this.role = role;
        this.otpHash = otp;
        this.exp = exp;
    }
}
