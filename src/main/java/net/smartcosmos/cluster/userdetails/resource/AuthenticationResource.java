package net.smartcosmos.cluster.userdetails.resource;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.smartcosmos.annotation.SmartCosmosRdao;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateRequest;
import net.smartcosmos.cluster.userdetails.service.AuthenticationService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@SmartCosmosRdao
@RestController
@Slf4j
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationResource(AuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    @RequestMapping(value = "authenticate", method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE,
                    consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> authenticate(
        @RequestBody @Valid RestAuthenticateRequest authenticate) {

        return authenticationService.authenticate(authenticate);
    }
}
