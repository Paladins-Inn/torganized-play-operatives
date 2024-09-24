package de.paladinsinn.tp.dcis.operatives.persistence;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;

@Mapper
public interface OperativeToJpa extends Function<Operative, OperativeJPA> {
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "revId", ignore = true)
    @Mapping(target = "revisioned", ignore = true)
    OperativeJPA apply(Operative orig);

    List<OperativeJPA> map(List<? extends Operative> orig);
}
