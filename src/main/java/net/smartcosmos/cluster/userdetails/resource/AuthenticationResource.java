package net.smartcosmos.cluster.userdetails.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.cluster.userdetails.domain.AuthenticateUserRequest;
import net.smartcosmos.cluster.userdetails.service.AuthenticateUserService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@SmartCosmosRdao
public class AuthenticationResource {

    private final AuthenticateUserService service;

    @Autowired
    public AuthenticationResource(AuthenticateUserService authenticateUserService) {

        this.service = authenticateUserService;
    }

    @RequestMapping(value = "authenticate",
                    method = RequestMethod.POST,
                    produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody @Valid AuthenticateUserRequest requestBody) {

        return service.authenticateUser(requestBody);
    }
}
