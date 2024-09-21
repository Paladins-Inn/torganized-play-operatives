package de.paladinsinn.tp.dcis.operatives.persistence;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntry;
import jakarta.annotation.Nullable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

/**
 * The Storm Knights are the PCs of the Torganized Play.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0
 */
@Entity
@Table(
    name = "OPERATIVES",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"UID"}),
        @UniqueConstraint(columnNames = {"NAMESPACE", "NAME"})
    }
)
@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = {"uid"})
@Slf4j
public class OperativeJPA implements HasId, HasNameSpace, Operative {
    /** The Database ID of the players account. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operatives_seq")
    @SequenceGenerator(name = "operatives_seq", sequenceName = "OPERATIVES_ID_SEQ", allocationSize = 10)
    @Column(name = "ID", columnDefinition = "BIGINT", unique = true, insertable = true, updatable = false)
    @ToString.Include
    private Long id;

    /** The UID of the player. */
    @NotNull
    @Column(name = "UID", columnDefinition = "UUID", unique = true, nullable = false, insertable = true, updatable = false)
    @Default
    @ToString.Include
    private UUID uid = UUID.randomUUID();

    /** Data set creation timestamp. */
    @Nullable
    @Column(name = "CREATED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = false)
    @ToString.Include
    private OffsetDateTime created;

    /** Last modification to this data set. */
    @Nullable
    @Column(name = "MODIFIED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = true)
    @ToString.Include
    private OffsetDateTime modified;

    /** Deletion date of this data set. */
    @Nullable
    @Column(name = "DELETED", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true, insertable = false, updatable = true)
    @ToString.Include
    private OffsetDateTime deleted;

    /** The namespace this storm knight belongs to. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the storm knights. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;

    /** The cosm this storm knight is from. */
    @NotNull
    @Column(name = "COSM", columnDefinition = "VARCHAR(100)", unique = false, nullable = false, insertable = true, updatable = true)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Default
    private Cosm cosm = Cosm.CORE_EARTH;

    /** The XP this storm knight has accumulated. */
    @NotNull
    @Column(name = "XP", columnDefinition = "BIGINT", unique = false, nullable = false, insertable = true, updatable = true)
    @ToString.Include
    @Default
    private long xp = 0;

    /** The money this storm knight has accumulated. */
    @NotNull
    @Column(name = "MONEY", columnDefinition = "BIGINT", unique = false, nullable = false, insertable = true, updatable = true)
    @ToString.Include
    @Default
    private long money = 0;

    /** Public description of this storm knight (accessable to everyone). */
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
        created = OffsetDateTime.now(Clock.systemUTC());
        modified = created;

        if (uid == null) {
            uid = UUID.randomUUID();
        }

        log.debug("Registering new operative. operative={}", this);
    }

    @PreUpdate
    public void preUpdate() {
        modified = OffsetDateTime.now(Clock.systemUTC());
        log.debug("Updating data for operative. operative={}", this);
    }

    @PreRemove
    public void preRemove() {
        log.debug("Removing data of operative. operative={}", this);
    }
}
