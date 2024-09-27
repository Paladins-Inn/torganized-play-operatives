package de.paladinsinn.tp.dcis.operatives.domain.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeImpl;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeToImpl;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeRepository;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeToJpa;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Service
@Builder(toBuilder = true, setterPrefix = "")
@RequiredArgsConstructor
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public class OperativeService {
    private final OperativeRepository jpaRepository;
    private final OperativeToImpl toModel;
    private final OperativeToJpa toJPA;

    public Optional<Operative> findByUid(final UUID uid) {
        Operative result = toModel.apply(jpaRepository.findById(uid).get());

        log.debug("Loaded operative by uid. uid={}, operative={}", uid, result);
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("java:S1452") // I want to return the interface but the framework will use the implementation.
    public List<? extends Operative> findByOwner(final String owner) {
        List<? extends Operative> result = toModel.apply(jpaRepository.findByOwner(owner));

        log.debug("Loaded all operatives of a specified owner. owner='{}', operatives={}", owner, result);
        return result;
    }

    @SuppressWarnings("java:S1452") // I want to return the interface but the framework will use the implementation.
    public Page<? extends Operative> findByOwner(final String owner, Pageable pageable) {
        Page<OperativeJPA> data = jpaRepository.findByOwner(owner, pageable);
        Page<? extends Operative> result = new PageImpl<>(toModel.apply(data.getContent()), data.getPageable(), data.getTotalElements());

        log.debug("Loaded a page of operatives of a specified owner. owner='{}', operatives={}, page={}/{}, index={}, size={}", owner, result.getContent(), 
                result.getPageable().getPageNumber(), result.getTotalPages(), result.getPageable().getOffset(), result.getPageable().getPageSize());
        return result;
    }

    public Operative update(Operative operative) {
        final OperativeJPA toSave = toJPA.apply(operative);
        Optional<OperativeJPA> orig = loadOperativeFromPersistence(operative.getId());
        orig.ifPresent(o -> transferPersistenceId(toSave, o));

        Operative result = toModel.apply(jpaRepository.saveAndFlush(toSave));
        log.debug("Updated operative. old={}, new={}", orig, result);
        return result;
    }

    private void transferPersistenceId(OperativeJPA target, final OperativeJPA source) {
        target.setId(source.getId());
    }

    private Optional<OperativeJPA> loadOperativeFromPersistence(final UUID uid) {
        Optional<OperativeJPA> result = jpaRepository.findById(uid);
        log.trace("Loaded operative from persistence. uid={}, operative={}", uid, result);

        return result;
    }

    public void retire(Operative operative) {
        OperativeJPA toSave = toJPA.apply(operative);
        Optional<OperativeJPA> orig = loadOperativeFromPersistence(operative.getId());
        orig.ifPresent(o -> transferPersistenceId(toSave, o));

        toSave.retire();


        log.debug("Retired operative. operative={}", operative);
    }

    public void delete(Operative operative) {
        jpaRepository.deleteById(operative.getId());;

        log.debug("Deleted operative. operative={}", operative);
    }

    public Operative protectOperativeData(final Operative operative, final Principal user) {
        if (hasRole(user, Set.of("ROLE_ORGA", "ROLE_ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return operative;
        }

        Optional<Operative> orig = findByUid(operative.getId());
        if (orig.isEmpty()) return operative;

        OperativeImpl data = toModel.apply(orig.get());
        return data.toBuilder()
                .name(operative.getName())
                .build();
    }

    private boolean hasRole(Principal user, Set<String> roles) {
        log.info("Checking roles. principal={}", user);

        return roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r)));
    }
}
