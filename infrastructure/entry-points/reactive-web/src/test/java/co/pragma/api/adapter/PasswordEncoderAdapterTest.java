package co.pragma.api.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderAdapter adapter;

    @Test
    void matchesShouldDelegateToPasswordEncoder() {
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);

        boolean result = adapter.matches("rawPass", "encodedPass");

        assertThat(result).isTrue();
        verify(passwordEncoder).matches("rawPass", "encodedPass");
    }

    @Test
    void encodeReactiveShouldReturnEncodedPassword() {
        when(passwordEncoder.encode("mySecret")).thenReturn("encodedSecret");

        StepVerifier.create(adapter.encodeReactive("mySecret"))
                .expectNext("encodedSecret")
                .verifyComplete();

        verify(passwordEncoder).encode("mySecret");
    }

    @Test
    void encodeReactiveShouldPropagateErrorWhenEncoderFails() {
        when(passwordEncoder.encode("bad")).thenThrow(new IllegalArgumentException("invalid"));

        StepVerifier.create(adapter.encodeReactive("bad"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(passwordEncoder).encode("bad");
    }
}
