/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package de.paladinsinn.tp.dcis.operatives.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import de.paladinsinn.tp.dcis.operatives.domain.converter.OperativeToImpl;
import de.paladinsinn.tp.dcis.operatives.domain.converter.OperativeToJpa;
import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@SuppressWarnings("unused")
@Service
@Builder(toBuilder = true)
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@XSlf4j
public class OperativeService {
    private final OperativeRepository jpaRepository;
    private final OperativeToImpl toModel;
    private final OperativeToJpa toJPA;

    public Optional<Operative> findById(final UUID uid) {
        log.entry(uid);

        Operative result = toModel.apply(jpaRepository.findById(uid).orElse(null));

        log.debug("Loaded operative by uid. uid={}, operative={}", uid, result);
        return log.exit(Optional.ofNullable(result));
    }

    @SuppressWarnings("java:S1452") // I want to return the interface but the framework will use the implementation.
    public List<? extends Operative> findByOwner(final String owner) {
        log.entry(owner);

        List<? extends Operative> result = toModel.apply(jpaRepository.findByOwner(owner));

        log.debug("Loaded all operatives of a specified owner. owner='{}', operatives={}", owner, result);
        return log.exit(result);
    }

    @SuppressWarnings("java:S1452") // I want to return the interface but the framework will use the implementation.
    public Page<? extends Operative> findByOwner(final String owner, Pageable pageable) {
        log.entry(owner, pageable);

        Page<OperativeJPA> data = jpaRepository.findByOwner(owner, pageable);
        Page<? extends Operative> result = new PageImpl<>(toModel.apply(data.getContent()), data.getPageable(), data.getTotalElements());

        log.debug("Loaded a page of operatives of a specified owner. owner='{}', operatives={}, page={}/{}, index={}, size={}", owner, result.getContent(), 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getPageable().getOffset(), result.getPageable().getPageSize());
        return log.exit(result);
    }

    public Operative update(Operative operative) {
        log.entry(operative);

        final OperativeJPA toSave = toJPA.apply(operative);
        Optional<OperativeJPA> orig = loadOperativeFromPersistence(operative.getId());
        orig.ifPresent(o -> transferPersistenceId(toSave, o));

        Operative result = toModel.apply(jpaRepository.saveAndFlush(toSave));
        log.debug("Updated operative. old={}, new={}", orig, result);
        return log.exit(result);
    }

    private void transferPersistenceId(OperativeJPA target, final OperativeJPA source) {
        target.setId(source.getId());
    }

    private Optional<OperativeJPA> loadOperativeFromPersistence(final UUID uid) {
        log.entry(uid);

        Optional<OperativeJPA> result = jpaRepository.findById(uid);
        log.trace("Loaded operative from persistence. uid={}, operative={}", uid, result);

        return log.exit(result);
    }

    public void retire(Operative operative) {
        log.entry(operative);

        OperativeJPA toSave = toJPA.apply(operative);
        Optional<OperativeJPA> orig = loadOperativeFromPersistence(operative.getId());
        orig.ifPresent(o -> transferPersistenceId(toSave, o));

        toSave.retire();


        log.debug("Retired operative. operative={}", operative);
    }

    public void delete(Operative operative) {
        log.entry(operative);

        Optional<OperativeJPA> toDelete = jpaRepository.findById(operative.getId());
        toDelete.ifPresent(jpaRepository::delete);

        log.debug("Deleted operative. operative={}", operative);
    }
}
