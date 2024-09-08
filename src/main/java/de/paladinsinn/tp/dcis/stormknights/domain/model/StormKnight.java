package de.paladinsinn.tp.dcis.stormknights.domain.model;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
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
    name = "STORMKNIGHTS",
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
public class StormKnight implements HasId, HasNameSpace, HasName {
    /** The Database ID of the players account. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stormknights_seq")
    @SequenceGenerator(name = "stormknights_seq", sequenceName = "STORMKNIGHTS_ID_SEQ", allocationSize = 10)
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

    @Nullable
    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(5000)", unique = false, nullable = true, insertable = true, updatable = true)
    private String description;

    @Nullable
    @Column(name = "NOTES", columnDefinition = "VARCHAR(5000)", unique = false, nullable = true, insertable = true, updatable = true)
    private String notes;

    /** The personal file of the storm knight. */
    @ElementCollection
    @CollectionTable(
        name = "STORMKNIGHTS_HISTORY", 
        joinColumns = @JoinColumn(name = "STORMKNIGHT", table = "STORMKNIGHTS_HISTORY", referencedColumnName = "ID")
    )
    @SuppressWarnings("java:S1948") // my implementations are serializable ...
    @Default
    private List<StormKnightHistoryEntry> history = new LinkedList<>();

    /** The number of missions this storm knight has been on. */
    public int getNoOfMissions() {
        return history.size();
    }

    public Optional<OffsetDateTime> getLastMissionDate() {
        return Optional.ofNullable(history.isEmpty() ? null : history.get(history.size()-1).getMissionDate());
    }

    public Clearance getClearance() {
        return Clearance.valueOf((int)xp);
    }

    
    @PrePersist
    public void prePersist() {
        created = OffsetDateTime.now(Clock.systemUTC());
        modified = created;

        if (uid == null) {
            uid = UUID.randomUUID();
        }

        log.debug("Creating storm knight. knight={}", this);
    }

    @PreUpdate
    public void preUpdate() {
        modified = OffsetDateTime.now(Clock.systemUTC());
        log.debug("Updating storm knight. knight={}", this);
    }
}
