package de.paladinsinn.tp.dcis.operatives.domain.model;

import java.util.List;
import java.util.function.Function;

import org.mapstruct.Mapper;

@Mapper
public interface OperativeToImpl extends Function<Operative, OperativeImpl> {
    OperativeImpl apply(Operative orig);
    List<OperativeImpl> apply(List<? extends Operative> orig);
}
