package de.paladinsinn.tp.dcis.operatives.domain.converter;

import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntryImpl;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeHistoryEntryJPA;

@Mapper
public interface OperativeHistoryToJpa extends Function<OperativeHistoryEntryImpl, OperativeHistoryEntryJPA> {
    OperativeHistoryEntryJPA apply(OperativeHistoryEntryImpl orig);
}
