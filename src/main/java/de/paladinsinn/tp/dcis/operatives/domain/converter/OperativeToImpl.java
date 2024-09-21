package de.paladinsinn.tp.dcis.operatives.domain.converter;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeImpl;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;

@Mapper
public interface OperativeToImpl extends Function<OperativeJPA, OperativeImpl> {
    OperativeImpl apply(OperativeJPA orig);
    List<OperativeImpl> apply(List<OperativeJPA> orig);
}
