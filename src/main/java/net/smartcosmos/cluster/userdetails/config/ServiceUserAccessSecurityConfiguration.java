package net.smartcosmos.cluster.userdetails.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import net.smartcosmos.extension.tenant.auth.provider.ServiceUserAccessAuthenticationProvider;

@Configuration
@Order(2)
@EnableConfigurationProperties(ServiceUserProperties.class)
@ComponentScan("net.smartcosmos.extension.tenant.auth")
public class ServiceUserAccessSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ServiceUserAccessAuthenticationProvider serviceUserAccessAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(serviceUserAccessAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http // @formatter:off
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/authenticate/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .httpBasic();
        // @formatter:on
    }
}
