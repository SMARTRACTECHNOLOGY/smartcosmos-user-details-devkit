package net.smartcosmos.test.config;

import static org.mockito.Mockito.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.smartcosmos.cluster.userdetails.service.AuthenticationService;

@Configuration
public class ResourceTestConfiguration {

    @Bean
    public AuthenticationService authenticationService() {

        return mock(AuthenticationService.class);
    }
}
