package net.smartcosmos.extension.tenant.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Initially created by SMART COSMOS Team on July 06, 2016.
 */
@Entity(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@Table(name = "role", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "tenantId" }))
@EntityListeners({ AuditingEntityListener.class })

public class RoleEntity {

    private static final int UUID_LENGTH = 16;
    private static final int STRING_FIELD_LENGTH = 255;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-binary")
    @Column(name = "id", length = UUID_LENGTH)
    private UUID id;

    @NotNull
    @Type(type = "uuid-binary")
    @Column(name = "tenantId", length = UUID_LENGTH, nullable = false, updatable = false)
    private UUID tenantId;

    @NotEmpty
    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "name", length = STRING_FIELD_LENGTH, nullable = false, updatable = false)
    private String name;

    @Size(max = STRING_FIELD_LENGTH)
    @Column(name = "description", length = STRING_FIELD_LENGTH, nullable = true, updatable = true)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<UserEntity> users;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "role_authorities",
               joinColumns = { @JoinColumn(name = "role") },
               inverseJoinColumns = { @JoinColumn(name = "authority") })
    private Set<AuthorityEntity> authorities;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", insertable = true, updatable = false)
    private Date created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastModified", nullable = false, insertable = true, updatable = true)
    private Date lastModified;

    @Basic
    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    /*
    Lombok's @Builder is not able to deal with field initialization default values. That's a known issue which won't get fixed:
    https://github.com/rzwitserloot/lombok/issues/663

    We therefore provide our own AllArgsConstructor that is used by the generated builder and takes care of field initialization.
 */
    @Builder
    @ConstructorProperties({ "id", "tenantId", "name", "description", "users", "authorities", "active" })
    protected RoleEntity(
        UUID id,
        UUID tenantId,
        String name,
        String description,
        Set<UserEntity> users,
        Set<AuthorityEntity> authorities,
        Boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.description = description;
        this.users = new HashSet<>();
        if (users != null && !users.isEmpty()) {
            this.users.addAll(users);
        }
        this.authorities = new HashSet<>();
        if (authorities != null && !authorities.isEmpty()) {
            this.authorities.addAll(authorities);
        }
        this.active = active != null ? active : true;
    }

}
