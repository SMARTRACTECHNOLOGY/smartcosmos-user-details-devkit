package net.smartcosmos.cluster.userdetails;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.cluster.userdetails.config.ServiceUserAccessSecurityConfiguration;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@Import(ServiceUserAccessSecurityConfiguration.class)
@Slf4j
public class UserDetailsService extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(UserDetailsService.class).web(true).run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
