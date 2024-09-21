package de.paladinsinn.tp.dcis.operatives.domain.converter;

import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeHistoryEntryImpl;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeHistoryEntryJPA;

@Mapper
public interface OperativeHistoryToImpl extends Function<OperativeHistoryEntryJPA, OperativeHistoryEntryImpl> {
    OperativeHistoryEntryImpl apply(OperativeHistoryEntryJPA orig);
}
