package net.smartcosmos.cluster.userdetails;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.annotation.EnableSmartCosmosMonitoring;
import net.smartcosmos.cluster.userdetails.config.ServiceUserAccessSecurityConfiguration;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@EnableSmartCosmosMonitoring
@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
@Import(ServiceUserAccessSecurityConfiguration.class)
@Slf4j
public class DevKitUserDetailsService {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DevKitUserDetailsService.class).web(true)
            .run(args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {

        return new LocalValidatorFactoryBean();
    }
}
