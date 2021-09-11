package ml.ridex.ridexapi.helper;

import com.google.common.hash.Hashing;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CustomHash {
    @Getter
    private final UUID uuid;
    @Getter
    private final String uuidHash;

    public CustomHash() {
        this(UUID.randomUUID());
    }

    public CustomHash(UUID uuid) {
        this.uuid = uuid;
        this.uuidHash = Hashing
                .sha256()
                .hashString(this.uuid.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public boolean verifyHash(String hash) {
        return this.uuidHash == hash;
    }
}
