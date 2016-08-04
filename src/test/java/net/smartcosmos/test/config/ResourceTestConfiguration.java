package net.smartcosmos.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.smartcosmos.cluster.userdetails.service.AuthenticationService;

import static org.mockito.Mockito.mock;

@Configuration
public class ResourceTestConfiguration {

    @Bean
    public AuthenticationService authenticationService() {

        return mock(AuthenticationService.class);
    }
}
