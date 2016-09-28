package net.smartcosmos.cluster.userdetails.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import net.smartcosmos.cluster.userdetails.domain.MessageResponse;
import net.smartcosmos.cluster.userdetails.domain.UserDetails;

import static net.smartcosmos.cluster.userdetails.domain.MessageResponse.CODE_ERROR;

/**
 * Utility class for Response Entity creation.
 */
public class ResponseEntityFactory {

    /**
     * Success response for user details.
     *
     * @param userDetails the user details
     * @return the response entity
     */
    public static ResponseEntity<?> success(UserDetails userDetails) {

        return ResponseEntity.ok(userDetails);
    }

    /**
     * Bad Request error response for invalid user credentials.
     * <pre>{ "code": 1, "message": "Invalid username or password" }</pre>
     *
     * @return the response entity
     */
    public static ResponseEntity<?> invalidUsernameOrPassword() {

        return ResponseEntity.badRequest()
            .body(new MessageResponse(CODE_ERROR, "Invalid username or password"));
    }

    /**
     * Internal Server Error response for invalid data retrieved from the user details provider.
     * <pre>{ "code": 1, "message": "Invalid data returned" }</pre>
     *
     * @return the response entity
     */
    public static ResponseEntity<?> invalidDataReturned() {

        return ResponseEntity.badRequest()
            .body(new MessageResponse(CODE_ERROR, "Invalid data returned"));
    }

    /**
     * General error response.
     * <pre>{ "code": code, "message": message }</pre>
     *
     * @param httpStatus the HTTP status code
     * @param code the error code
     * @param message the error message
     * @return the response entity
     */
    public static ResponseEntity<?> errorResponse(HttpStatus httpStatus, Integer code, String message) {

        return ResponseEntity.status(httpStatus)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .body(new MessageResponse(code, message));
    }
}
