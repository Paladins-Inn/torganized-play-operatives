package de.paladinsinn.tp.dcis.operatives.persistence;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import de.kaiserpfalzedv.commons.jpa.AbstractJPAEntity;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntry;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.ToString.Include;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

/**
 * The Operatives are the PCs of the Torganized Play.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@Entity
@Audited
@AuditTable("OPERATIVES_AUD")
@Table(
    name = "OPERATIVES",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@SuperBuilder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class OperativeJPA extends AbstractJPAEntity<UUID> implements Operative {
    /** The namespace this storm knight belongs to. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @Include
    private String nameSpace;

    /** The name of the Operatives. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @Include
    private String name;

    @NotNull
    @Column(name = "OWNER", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.")
    @Include
    private String owner;

    /** The cosm this storm knight is from. */
    @NotNull
    @Column(name = "COSM", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    @Include
    @Default
    private Cosm cosm = Cosm.CORE_EARTH;

    /** The XP this storm knight has accumulated. */
    @NotNull
    @Column(name = "XP", columnDefinition = "BIGINT", unique = false, nullable = false, insertable = true, updatable = true)
    @Include
    @Default
    private long xp = 0;

    /** The money this storm knight has accumulated. */
    @NotNull
    @Column(name = "MONEY", columnDefinition = "BIGINT", unique = false, nullable = false, insertable = true, updatable = true)
    @Include
    @Default
    private long money = 0;

    /** Public description of this storm knight (accessible to everyone). */
    @Nullable
    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(5000)", unique = false, nullable = true, insertable = true, updatable = true)
    private String description;

    /** Delphi Council internal notes relating to the storm knight (accessible by the player, GMs he plays with, orga, judges and admins). */
    @Nullable
    @Column(name = "NOTES", columnDefinition = "VARCHAR(5000)", unique = false, nullable = true, insertable = true, updatable = true)
    private String notes;

    /** The personal file of the storm knight. */
    @ElementCollection(targetClass = OperativeHistoryEntryJPA.class)
    @CollectionTable(
        name = "OPERATIVES_HISTORY", 
        joinColumns = @JoinColumn(name = "OPERATIVE", table = "OPERATIVES_HISTORY", referencedColumnName = "ID")
    )
    @SuppressWarnings("java:S1948") // my implementations are serializable ...
    @Default
    private List<OperativeHistoryEntry> history = new LinkedList<>();

    @Override
    public Clearance getClearance() {
        return Clearance.valueOf((int)xp);
    }

    public void retire() {
        deleted = OffsetDateTime.now(Clock.systemUTC());
        log.info("Operative retired. operative={}, retirement={}", this, deleted);
    }

    
    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        log.debug("Registering new operative. operative={}", this);
    }

    @PreRemove
    public void preRemove() {
        log.debug("Removing data of operative. operative={}", this);
    }
}
