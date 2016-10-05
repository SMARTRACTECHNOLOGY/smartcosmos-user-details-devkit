package net.smartcosmos.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.smartcosmos.userdetails.service.AuthenticateUserService;

import static org.mockito.Mockito.mock;

@Configuration
public class ResourceTestConfiguration {

    @Bean
    public AuthenticateUserService authenticateUserService() {

        return mock(AuthenticateUserService.class);
    }
}
