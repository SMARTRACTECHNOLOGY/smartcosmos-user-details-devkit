package net.smartcosmos.cluster.userdetails.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for REST code/message responses.
 */
@JsonIgnoreProperties({ "version" })
@Data
@Builder
@AllArgsConstructor
public class MessageResponse {

    private static final int VERSION = 1;
    private final int version = VERSION;

    private Integer code;
    private String message;

}