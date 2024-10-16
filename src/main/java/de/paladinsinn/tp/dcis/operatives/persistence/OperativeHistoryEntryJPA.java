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
package de.paladinsinn.tp.dcis.operatives.persistence;


import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntry;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
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
 * a single history entry for the Operatives HR file.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-08-24
 */
@Embeddable
@Audited
@AuditTable("OPERATIVE_HISTORY")
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, setterPrefix = "")
@Data
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
@EqualsAndHashCode(of = {"missionUid"})
@Slf4j
public class OperativeHistoryEntryJPA implements OperativeHistoryEntry {
        /** Data set creation timestamp. */
        @Nullable
        @CreatedDate
        @Column(name = "CREATED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = false)
        @ToString.Include
        private OffsetDateTime created;
    
        /** Last modification to this data set. */
        @Nullable
        @LastModifiedDate
        @Column(name = "MODIFIED", columnDefinition = "TIMESTAMP WITH TIME ZONE", unique = false, nullable = false, insertable = true, updatable = true)
        private OffsetDateTime modified;
    
        /** Deletion date of this data set. */
        @Nullable
        @Column(name = "DELETED", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true, insertable = false, updatable = true)
        @ToString.Include
        private OffsetDateTime deleted;

        @NotNull
        @Column(name = "MISSION_NAME", columnDefinition = "VARCHAR(100)", nullable = false, insertable = true, updatable = true)
        private String missionName;

        @NotNull
        @Column(name = "MISSION_DATE", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true, insertable = false, updatable = true)
        private OffsetDateTime missionDate;

        @NotNull
        @Column(name = "MISSION_UID", columnDefinition = "UUID", nullable = false, insertable = true, updatable = true)
        private UUID missionUid;

        @NotNull
        @Column(name = "XP", columnDefinition = "BIGINT", nullable = false, insertable = true, updatable = true)
        @Default
        private long xp = 5;

        @NotNull
        @Column(name = "PAYMENT", columnDefinition = "BIGINT", nullable = false, insertable = true, updatable = true)
        @Default
        private long payment = 200;

        @NotNull
        @Column(name = "REPORT", columnDefinition = "VARCHAR(5000)", nullable = false, insertable = true, updatable = true)
        private String report;

        @NotNull
        @Enumerated(EnumType.STRING)
        @Column(name = "SUCCESS", columnDefinition = "VARCHAR(100)", nullable = false, insertable = true, updatable = true)
        @Default
        private SuccessState success = SuccessState.NONE;
    }
