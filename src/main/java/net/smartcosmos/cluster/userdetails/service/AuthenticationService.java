package net.smartcosmos.cluster.userdetails.service;

import java.util.Optional;
import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.smartcosmos.extension.tenant.dao.TenantDao;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateRequest;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateResponse;
import net.smartcosmos.security.user.SmartCosmosUser;

@Service
public class AuthenticationService {

    private final TenantDao UserDetailsDao;
    private final ConversionService conversionService;

    @Inject
    public AuthenticationService(TenantDao UserDetailsDao, ConversionService conversionService) {

        this.UserDetailsDao = UserDetailsDao;
        this.conversionService = conversionService;
    }

    public ResponseEntity<?> authenticate(RestAuthenticateRequest authenticate, SmartCosmosUser user) {

        Optional<UserDetailsResponse> entity = UserDetailsDao.getAuthorities(authenticate.getName(), authenticate.getCredentials());
        if (entity.isPresent()) {

            return ResponseEntity.ok(conversionService.convert(entity.get(), RestAuthenticateResponse.class));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
