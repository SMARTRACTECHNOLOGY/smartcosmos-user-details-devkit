package net.smartcosmos.cluster.userdetails;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import net.smartcosmos.annotation.EnableSmartCosmosEvents;
import net.smartcosmos.annotation.EnableSmartCosmosExtension;
import net.smartcosmos.annotation.EnableSmartCosmosMonitoring;

@EnableSmartCosmosExtension
@EnableSmartCosmosEvents
@EnableSmartCosmosMonitoring
@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
@Slf4j
public class DevKitUserDetailsService {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DevKitUserDetailsService.class).web(true)
            .run(args);
    }
}
