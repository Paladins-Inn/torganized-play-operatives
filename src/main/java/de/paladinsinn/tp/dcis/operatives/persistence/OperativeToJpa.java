package de.paladinsinn.tp.dcis.operatives.persistence;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;

import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeImpl;

@Mapper
public interface OperativeToJpa extends Function<OperativeImpl, OperativeJPA> {
    OperativeJPA apply(Operative orig);

    List<OperativeJPA> apply(List<? extends Operative> orig);
}
