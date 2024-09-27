package de.paladinsinn.tp.dcis.operatives.controller;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import de.kaiserpfalzedv.rpg.torg.model.core.Cosm;
import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.model.OperativeImpl;
import de.paladinsinn.tp.dcis.operatives.domain.service.OperativeService;
import groovy.lang.Binding;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ManageOperative {
    private static final String DATA_MODEL = "operative";

    private final OperativeService operativeService;

    @GetMapping("/")
    @RolesAllowed("PLAYER")
    public String createNewOperative(
        @NotNull Principal principal,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer,
        @NotNull Model model
    ) {

        log.info("Showing input form for new operatives. user={}", 
            principal.getName()
        );

        if (referrer == null) {
            referrer = "/" + principal.getName() + "/list";
        }

        Operative operative = OperativeImpl.builder()
            .id(UUID.randomUUID())
            .owner(principal.getName())
            .build();

        model.addAttribute("cosms", Cosm.values());
        model.addAttribute("errors", new Binding());
        model.addAttribute("referrer", referrer);
        model.addAttribute(DATA_MODEL, operative);
        return "edit-operative";
    }

    @PostMapping("/")
    @RolesAllowed("PLAYER")
    public String saveOperative(
        @NotNull Principal principal,
        @Valid @NotNull @ModelAttribute(DATA_MODEL) Operative operative,
        BindingResult binding,
        @NotNull Model model
    ) {
        log.info("Saving storm operative data. user={}, operative={}", principal.getName(), operative);

        model.addAttribute("errors", binding);
        if (binding.hasErrors() && binding.getAllErrors().size() > 1) {
            log.warn("Data is invalid. operative={}, errors={}", operative, binding.getAllErrors());

            model.addAttribute("cosms", Cosm.values());
            model.addAttribute("referrer", "/" + principal.getName() + "/list");
            model.addAttribute(DATA_MODEL, operative);
            return "edit-operative";
        }

        if (
            ! principal.getName().equals(operative.getOwner())
            && !hasRole(principal, Set.of("ROLE_ORGA","ROLE_ADMIN"))
        ) {
            log.warn("The storm operative is not owned by the user. No data will be changed. user={}, operative={}", principal, operative);
        } else {
            operative = operativeService.protectOperativeData(operative, principal);
            operative = operativeService.update(operative);

            log.info("Changed storm operative saved. operative={}", operative);
        }

        model.addAttribute(DATA_MODEL, operative);

        return "redirect:/" + principal.getName() + "/list";
    }

    private boolean hasRole(Principal user, Set<String> roles) {
        log.info("Checking roles. principal={}", user);

        return roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r)));
    }
}
