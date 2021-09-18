package ml.ridex.ridexapi.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Otp {
    private final String otp;
    private final long exp;
}
