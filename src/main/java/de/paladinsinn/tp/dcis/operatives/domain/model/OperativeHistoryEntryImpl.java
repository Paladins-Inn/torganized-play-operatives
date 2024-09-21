package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import de.kaiserpfalzedv.rpg.torg.model.core.SuccessState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

@Jacksonized
@Builder(toBuilder = true, setterPrefix = "")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(of = {"missionUid"})
@Slf4j
public class OperativeHistoryEntryImpl implements OperativeHistoryEntry {
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime deleted;

    private UUID missionUid;
    private String missionName;
    private OffsetDateTime missionDate;

    private long xp;
    private long payment;

    private SuccessState success;
    private String report;
}
