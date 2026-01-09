package healthcareab.project.healthcare_booking_app;

import healthcareab.project.healthcare_booking_app.config.SecurityConfig;
import healthcareab.project.healthcare_booking_app.filters.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Configuration
    static class TestConfig {
        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter() {
            return mock(JwtAuthenticationFilter.class); // provide a mock
        }
    }
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SecurityConfig.class, TestConfig.class);

    @Test
    void securityConfig_shouldLoadBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SecurityConfig.class);
            assertThat(context).hasSingleBean(AuthenticationManager.class);
            assertThat(context).hasSingleBean(SecurityFilterChain.class);
            assertThat(context).hasSingleBean(PasswordEncoder.class);
        });
    }
}