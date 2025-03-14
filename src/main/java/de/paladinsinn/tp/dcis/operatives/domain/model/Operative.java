/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.kaiserpfalzedv.commons.api.resources.*;
import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import jakarta.annotation.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Operative - The Storm Knights or Stormers involved in the game.
 *
 * @author klenkes74
 * @since 2024-09-24
 */
@SuppressWarnings("unused") // it's a library interface
@JsonDeserialize(as = OperativeImpl.class)
public interface Operative extends HasId<UUID>, HasNameSpace, HasName, HasTimestamps {
    Cosm getCosm();

    long getXp();

    long getMoney();

    @Nullable
    String getDescription();

    @Nullable
    String getNotes();

    List<OperativeHistoryEntry> getHistory();

    Clearance getClearance();

    /** The number of missions this operative has been on. */
    default int getNoOfMissions() {
        return getHistory().size();
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    default OffsetDateTime getLastMissionDate() {
        return (getHistory() == null || getHistory().isEmpty()) ? null : getHistory().get(getHistory().size()-1).getMissionDate();
    }
}