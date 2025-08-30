package main.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(
        jsr250Enabled = true,
        securedEnabled = true
)
public class MethodSecurityConfig {
}
