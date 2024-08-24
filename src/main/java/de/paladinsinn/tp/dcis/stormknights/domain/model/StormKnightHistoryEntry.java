/*
 * Copyright (c) 2024 Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.paladinsinn.tp.dcis.stormknights.domain.model;


import java.time.OffsetDateTime;
import java.time.ZoneId;

import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;


/**
 * a single history entry for the storm knights HR file.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-08-24
 */
@Entity
@Table(
    name = "SKHISTORY",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID"}),
        @UniqueConstraint(columnNames = {"STORMKNIGHT", "MISSION_UID"})
    }
)
@Jacksonized
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(of = {"id"})
@Slf4j
public class StormKnightHistoryEntry {
        /** The Database ID history entry. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "ID", columnDefinition = "BIGINT", unique = true, nullable = false, insertable = true, updatable = false)
        @ToString.Include
        private Long id;
    
        /** Data set creation timestamp. */
        @Default
        @NotNull
        @Column(name = "CREATED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = false)
        @ToString.Include
        private OffsetDateTime created = OffsetDateTime.now(ZoneId.of("UTC"));
    
        /** Last modification to this data set. */
        @Default
        @NotNull
        @Column(name = "MODIFIED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = true)
        @ToString.Include
        private OffsetDateTime modified = OffsetDateTime.now(ZoneId.of("UTC"));
    
        /** Deletion date of this data set. */
        @Nullable
        @Column(name = "DELETED", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true, insertable = false, updatable = true)
        @ToString.Include
        private OffsetDateTime deleted;

        @NotNull
        @ManyToOne(optional = false, fetch = FetchType .EAGER)
        @JoinColumn(name = "STORMKNIGHT", columnDefinition = "VARCHAR(100)", nullable = false, insertable = true, updatable = false)
        private StormKnight stormKnight;

        @NotNull
        @Column(name = "MISSION_NAME", columnDefinition = "VARCHAR(100)", nullable = false, insertable = true, updatable = true)
        private String missionName;

        @NotNull
        @Column(name = "MISSION_DATE", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true, insertable = false, updatable = true)
        private OffsetDateTime missionDate;

        @NotNull
        @Column(name = "MISSION_UID", columnDefinition = "UUID", nullable = false, insertable = true, updatable = true)
        private String missionUid;

        @NotNull
        @Column(name = "XP", columnDefinition = "BIGINT", nullable = false, insertable = true, updatable = true)
        private long xp;

        @NotNull
        @Column(name = "PAYMENT", columnDefinition = "BIGINT", nullable = false, insertable = true, updatable = true)
        private long payment;

        @NotNull
        @Column(name = "REPORT", columnDefinition = "VARCHAR(5000)", nullable = false, insertable = true, updatable = true)
        private String report;

        @NotNull
        @Default
        @Column(name = "SUCCESS", columnDefinition = "VARCHAR(100)", nullable = false, insertable = true, updatable = true)
        private SuccessState success = SuccessState.NONE;
    }
