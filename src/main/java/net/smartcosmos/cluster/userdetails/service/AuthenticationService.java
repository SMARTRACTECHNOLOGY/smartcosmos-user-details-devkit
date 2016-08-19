package net.smartcosmos.cluster.userdetails.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import net.smartcosmos.cluster.userdetails.dao.UserDetailsDao;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateRequest;
import net.smartcosmos.cluster.userdetails.dto.RestAuthenticateResponse;
import net.smartcosmos.cluster.userdetails.dto.UserDetailsResponse;

@Service
public class AuthenticationService {

    private final UserDetailsDao userDetailsDao;
    private final ConversionService conversionService;

    @Autowired
    public AuthenticationService(UserDetailsDao userDetailsDao, ConversionService conversionService) {

        this.userDetailsDao = userDetailsDao;
        this.conversionService = conversionService;
    }

    public ResponseEntity<?> authenticate(RestAuthenticateRequest authenticate) {

        if (authenticate == null || StringUtils.isEmpty(authenticate.getCredentials())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .build();
        }

        Optional<UserDetailsResponse> entity = userDetailsDao.getAuthorities(authenticate.getName(), authenticate.getCredentials());
        if (entity.isPresent()) {

            return ResponseEntity.ok(conversionService.convert(entity.get(), RestAuthenticateResponse.class));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .build();
    }
}
