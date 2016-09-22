package net.smartcosmos.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.smartcosmos.cluster.userdetails.service.AuthenticateUserService;

import static org.mockito.Mockito.mock;

@Configuration
public class ResourceTestConfiguration {

    @Bean
    public AuthenticateUserService authenticateUserServiceDevKit() {

        return mock(AuthenticateUserService.class);
    }
}
