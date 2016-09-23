package net.smartcosmos.cluster.userdetails.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfiguration {

    @Bean
    public javax.validation.Validator localValidatorFactoryBean() {

        return new LocalValidatorFactoryBean();
    }
}
