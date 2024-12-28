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
import java.time.ZoneOffset;
import java.util.UUID;

import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntry;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.XSlf4j;


/**
 * a single history entry for the storm knights HR file.
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @version 1.0.0
 * @since 2024-08-24
 */
@Embeddable
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@XSlf4j
public class OperativeHistoryEntryJPA implements OperativeHistoryEntry {

  @NotNull
  @Column(name = "CREATED", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
  private OffsetDateTime created;

  @Nullable
  @Column(name = "MODIFIED", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime modified;

  @Nullable
  @Column(name = "DELETED", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private OffsetDateTime deleted;

  @NotNull
  @Column(name = "MISSION_NAME", columnDefinition = "VARCHAR(100)", nullable = false)
  private String missionName;

  @NotNull
  @Column(name = "MISSION_DATE", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
  private OffsetDateTime missionDate;

  @NotNull
  @Column(name = "MISSION_UID", columnDefinition = "UUID", nullable = false)
  @Default
  private UUID missionUid = UUID.randomUUID();

  @NotNull
  @Column(name = "XP", columnDefinition = "BIGINT", nullable = false)
  @Default
  private long xp = 5;

  @NotNull
  @Column(name = "PAYMENT", columnDefinition = "BIGINT", nullable = false)
  @Default
  private long payment = 200;

  @NotNull
  @Column(name = "REPORT", columnDefinition = "VARCHAR(5000)", nullable = false)
  private String report;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "SUCCESS", columnDefinition = "VARCHAR(100)", nullable = false)
  @Default
  private SuccessState success = SuccessState.NONE;

  @PrePersist
  public void prePersist() {
    log.entry(this);

    created = modified = OffsetDateTime.now(ZoneOffset.UTC);

    log.exit();
  }

  @PreUpdate
  public void preUpdate() {
    log.entry(this);

    modified = OffsetDateTime.now(ZoneOffset.UTC);

    log.exit();
  }
}
