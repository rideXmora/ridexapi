package ml.ridex.ridexapi.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CustomHashTest {
    private CustomHash customHash;

    @Test
    @DisplayName("Test uuid hash generation")
    public void getUuidAndHash() {
        customHash = new CustomHash();
        assertThat(customHash.getUuid()).isInstanceOf(UUID.class);
        assertThat(customHash.getUuidHash()).asString();
    }
}
