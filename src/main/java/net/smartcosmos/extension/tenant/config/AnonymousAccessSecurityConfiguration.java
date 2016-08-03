package net.smartcosmos.extension.tenant.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import net.smartcosmos.extension.tenant.auth.SmartCosmosAnonymousUser;

@Configuration
@Order(SecurityProperties.DEFAULT_FILTER_ORDER)
public class AnonymousAccessSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http // @formatter:off
            .requestMatchers()
            .antMatchers("/tenants/**")
            .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/tenants").permitAll()
            .and()
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .anonymous()
                .key(SmartCosmosAnonymousUser.ANONYMOUS_AUTHENTICATION_KEY)
                .principal(SmartCosmosAnonymousUser.ANONYMOUS_USER)
            .and()
            .csrf()
                .disable();
        // @formatter:on
    }
}
