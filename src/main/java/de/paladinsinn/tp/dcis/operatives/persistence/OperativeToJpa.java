package de.paladinsinn.tp.dcis.operatives.persistence;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeImpl;

@Mapper
public interface OperativeToJpa extends Function<OperativeImpl, OperativeJPA> {
    @Mapping(target = "id", ignore = true) // the id has to be loaded from the database
    OperativeJPA apply(OperativeImpl orig);

    List<OperativeJPA> apply(List<? extends Operative> orig);

    @Mapping(target = "id", ignore = true) // the id has to be loaded from the database
    OperativeJPA apply(Operative orig);
}
