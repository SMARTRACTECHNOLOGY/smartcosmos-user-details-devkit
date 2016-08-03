package net.smartcosmos.extension.tenant.domain;

import java.beans.ConstructorProperties;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Initially created by SMART COSMOS Team on July 06, 2016.
 */
@Entity(name = "authority")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Table(name = "authority", uniqueConstraints = @UniqueConstraint(columnNames = { "authority" }))
@EntityListeners({ AuditingEntityListener.class })

public class AuthorityEntity {

    private static final int STRING_FIELD_LENGTH = 255;

    @Id
    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "authority", length = STRING_FIELD_LENGTH, nullable = false, updatable = false)
    private String authority;

    /*
Lombok's @Builder is not able to deal with field initialization default values. That's a known issue which won't get fixed:
https://github.com/rzwitserloot/lombok/issues/663

We therefore provide our own AllArgsConstructor that is used by the generated builder and takes care of field initialization.
*/
    @Builder
    @ConstructorProperties({ "authority" })
    protected AuthorityEntity(
        String authority) {
        this.authority = authority;
    }
}
