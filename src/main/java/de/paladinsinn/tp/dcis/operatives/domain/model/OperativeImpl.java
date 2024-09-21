package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.kaiserpfalzedv.rpg.torg.model.actors.Clearance;
import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
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
@EqualsAndHashCode(of = {"uid"})
@Slf4j
public class OperativeImpl implements Operative {
    private UUID uid;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private OffsetDateTime deleted;

    private String nameSpace;
    private String name;

    private Cosm cosm;
    private long xp;
    private long money;
    private Clearance clearance;

    private String description;
    private String notes;

    @Default
    private List<OperativeHistoryEntry> history = new LinkedList<>();
}
