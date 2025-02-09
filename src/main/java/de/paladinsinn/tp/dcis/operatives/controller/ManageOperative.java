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

package de.paladinsinn.tp.dcis.operatives.controller;

import java.security.Principal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import de.paladinsinn.tp.dcis.operatives.domain.service.OperativeService;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeRepository;
import groovy.lang.Binding;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * Manages the operatives for the different roles using the DCIS.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0-SNAPSHOT
 * @version 1.1.0-SNAPSHOT
 */
@RequiredArgsConstructor
@XSlf4j
@Controller
@RequestMapping("/")
public class ManageOperative {
    private static final String DATAMODEL = "operative";

    private final OperativeRepository operativesRepository;
    private final OperativeService operativeService;

    @Value("${server.servlet.contextPath}:/operatives")
    private String contextPath;

    @GetMapping("/")
    @RolesAllowed("PLAYER")
    public String createNewOperative(
        @NotNull Principal principal,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer,
        @NotNull Model model
    ) {
        log.entry(principal, referrer, model);

        log.info("Showing input form for new operatives. user={}",
            principal.getName()
        );
        
        Operative operative = OperativeJPA.builder()
            .id(UUID.randomUUID())
            .nameSpace(principal.getName())
            .created(OffsetDateTime.now(Clock.systemUTC()))
            .modified(OffsetDateTime.now(Clock.systemUTC()))
            .build();
        
        fillOperatorModelForTemplating(principal, referrer, model, operative);
        
        return log.exit("edit-operatives");
    }
    
    private void fillOperatorModelForTemplating(final Principal principal, final String referrer, final Model model, final Operative operative) {
        model.addAttribute("contextPath", contextPath);
        model.addAttribute("referrer", sanitizeReferrerIfNull(principal, referrer));
        model.addAttribute("cosms", Cosm.values());
        model.addAttribute("errors", new Binding());
        model.addAttribute(DATAMODEL, operative);
    }
    
    
    private static String sanitizeReferrerIfNull(final Principal principal, String referrer) {
        if (referrer == null) {
            referrer = "/" + principal.getName() + "/list";
        }
        return referrer;
    }
    
    
    @GetMapping("/{id}")
    @RolesAllowed("PLAYER")
    public String editOperative(
        @NotNull @PathVariable UUID id,
        @NotNull Principal principal,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer,
        @NotNull Model model
    ) {
        log.entry(id, principal, referrer, model);
        
        Optional<Operative> operative = operativeService.findById(id);
        
        if (operative.isEmpty()) {
            log.info("There is no operative with this id. Creating a new one instead. id={}", id);
            
            return log.exit(createNewOperative(principal, referrer, model));
        }
        
        fillOperatorModelForTemplating(principal, referrer, model, operative.get());
        
        return log.exit("edit-operatives");
    }
    
    
    @PostMapping("/")
    @RolesAllowed("PLAYER")
    public String saveOperative(
        @NotNull Principal principal,
        @Valid @NotNull @ModelAttribute(DATAMODEL) OperativeJPA operative,
        BindingResult binding,
        @NotNull Model model
    ) {
        log.entry(principal, operative, binding, model);

        if (binding.hasErrors() && binding.getAllErrors().size() > 1) {
            log.warn("Data is invalid. operative={}, errors={}", operative, binding.getAllErrors());
            
            fillOperatorModelForTemplating(principal, null, model, operative);
            
            return log.exit("edit-operatives");
        }

        if (
            ! principal.getName().equals(operative.getNameSpace())
            && !hasRole(principal, Set.of("ROLE_ORGA","ROLE_ADMIN"))
        ) {
            log.warn("The operative is not owned by the user. user={}, operative={}", principal, operative);
        } else {
            operative = protectKnightData(operative, principal);
            operative = operativesRepository.save(operative);

            log.info("Changed operative saved. operative={}", operative);
        }

        model.addAttribute(DATAMODEL, operative);

        return log.exit("redirect:/" + principal.getName() + "/list");
    }

    private OperativeJPA protectKnightData(final OperativeJPA operative, final Principal user) {
        log.entry(operative, user);

        if (hasRole(user, Set.of("ROLE_ORGA", "ROLE_ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return log.exit(operative);
        }

        Optional<OperativeJPA> orig = operativesRepository.findById(operative.getId());
        if (orig.isEmpty()) {
            return log.exit(null);
        }

        return log.exit(orig.get().toBuilder()
                .name(operative.getName())
                .build()
        );
    }
    
    private boolean hasRole(Principal user, Set<String> roles) {
        log.entry(user, roles);
        
        log.info("Checking roles. principal={}", user);
        
        return log.exit(roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r))));
    }
}
