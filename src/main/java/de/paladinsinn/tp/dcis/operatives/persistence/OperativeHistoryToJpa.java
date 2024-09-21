package de.paladinsinn.tp.dcis.operatives.persistence;

import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntryImpl;

@Mapper
public interface OperativeHistoryToJpa extends Function<OperativeHistoryEntryImpl, OperativeHistoryEntryJPA> {
    OperativeHistoryEntryJPA apply(OperativeHistoryEntryImpl orig);
}
