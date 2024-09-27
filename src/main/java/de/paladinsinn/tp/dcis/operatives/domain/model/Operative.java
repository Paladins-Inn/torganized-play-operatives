package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.HasId;
import de.kaiserpfalzedv.commons.api.resources.HasName;
import de.kaiserpfalzedv.commons.api.resources.HasNameSpace;
import de.kaiserpfalzedv.commons.api.resources.HasTimestamps;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import jakarta.annotation.Nullable;

@JsonDeserialize(as = OperativeImpl.class)
public interface Operative extends HasId<UUID>, HasName, HasNameSpace, HasTimestamps {
    String getOwner();
    
    Cosm getCosm();

    long getXp();

    long getMoney();

    @Nullable
    String getDescription();

    @Nullable
    String getNotes();

    List<OperativeHistoryEntry> getHistory();

    Clearance getClearance();

    /** The number of missions this storm knight has been on. */
    default int getNoOfMissions() {
        return getHistory().size();
    }

    default Optional<OffsetDateTime> getLastMissionDate() {
        return Optional.ofNullable(getHistory().isEmpty() ? null : getHistory().get(getHistory().size()-1).getMissionDate());
    }
}