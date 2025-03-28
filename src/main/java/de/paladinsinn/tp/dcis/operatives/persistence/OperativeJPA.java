package de.paladinsinn.tp.dcis.operatives.persistence;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.kaiserpfalzedv.commons.jpa.AbstractRevisionedJPAEntity;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;

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
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = true)
@XSlf4j
public class OperativeJPA  extends AbstractRevisionedJPAEntity<UUID> implements Operative {
    /** The namespace this operative belongs to. */
    @NotNull
    @Column(name = "NAMESPACE", columnDefinition = "VARCHAR(100)", nullable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String nameSpace;

    /** The name of the operatives. Needs to be unique within the namespace. */
    @NotNull
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)", nullable = false)
    @Size(min = 3, max = 100, message = "The length of the string must be between 3 and 100 characters long.") @Pattern(regexp = "^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$", message = "The string must match the pattern '^[a-zA-Z][-a-zA-Z0-9]{1,61}(.[a-zA-Z][-a-zA-Z0-9]{1,61}){0,4}$'")
    @ToString.Include
    private String name;

    /** The cosm this operative is from. */
    @NotNull
    @Column(name = "COSM", columnDefinition = "VARCHAR(100)", nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    @Default
    private Cosm cosm = Cosm.CORE_EARTH;

    /** The XP this operative has accumulated. */
    @NotNull
    @Column(name = "XP", columnDefinition = "BIGINT", nullable = false)
    @ToString.Include
    @Default
    private long xp = 0;

    /** The money this operative has accumulated. */
    @NotNull
    @Column(name = "MONEY", columnDefinition = "BIGINT", nullable = false)
    @ToString.Include
    @Default
    private long money = 0;

    /** Public description of this operative (accessible to everyone). */
    @Nullable
    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(5000)")
    private String description;

    /** Delphi Council internal notes relating to the operative (accessible by the player, GMs he plays with, orga, judges and admins). */
    @Nullable
    @Column(name = "NOTES", columnDefinition = "VARCHAR(5000)")
    private String notes;

    /** The personell file of the operative. */
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
        log.entry(this);

        deleted = OffsetDateTime.now(Clock.systemUTC());

        log.exit(this);
    }

    
    @PrePersist
    public void prePersist() {
        log.entry(this);
        created = modified = OffsetDateTime.now(Clock.systemUTC());

        if (id == null) {
            id = UUID.randomUUID();
        }

        log.exit(this);
    }

    @PreUpdate
    public void preUpdate() {
        log.entry(this);

        modified = OffsetDateTime.now(Clock.systemUTC());

        log.exit(this);
    }

    @PreRemove
    public void preRemove() {
        log.entry(this);
        log.exit();
    }
}
