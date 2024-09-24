package de.paladinsinn.tp.dcis.operatives.persistence;

import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntry;

@Mapper
public interface OperativeHistoryToJpa extends Function<OperativeHistoryEntry, OperativeHistoryEntryJPA> {
    OperativeHistoryEntryJPA apply(OperativeHistoryEntry orig);
}
