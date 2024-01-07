package de.sky.regular.income.server;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class CustomWebSecurityConfigurerAdapterTest {
    private final CustomWebSecurityConfigurerAdapter adapter = new CustomWebSecurityConfigurerAdapter();
    private final PasswordEncoder encoder = adapter.newPasswordEncoder();

    @Test
    void showEncoding() {
        assertThat(encoder.encode("my-password"))
                .isNotBlank()
                .isNotEqualTo("my-password");
    }
}
