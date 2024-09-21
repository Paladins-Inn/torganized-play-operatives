package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface OperativeHistoryToImpl extends Function<OperativeHistoryEntry, OperativeHistoryEntryImpl> {
    OperativeHistoryEntryImpl apply(OperativeHistoryEntry orig);
}
