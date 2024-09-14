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
import de.paladinsinn.tp.dcis.operatives.domain.service.OperativeRepository;
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
    private static final String DATAMODEL = "knight";

    private final OperativeRepository stormKnightRepository;

    @GetMapping("/")
    @RolesAllowed("PLAYER")
    public String createNewOperative(
        @NotNull Principal principal,
        @RequestHeader(value = HttpHeaders.REFERER, required = false) String referrer,
        @NotNull Model model
    ) {

        log.info("Showing input form for new storm knights. user={}", 
            principal.getName()
        );

        if (referrer == null) {
            referrer = "/" + principal.getName() + "/list";
        }

        Operative knight = Operative.builder()
            .uid(UUID.randomUUID())
            .nameSpace(principal.getName())
            .build();

        model.addAttribute("cosms", Cosm.values());
        model.addAttribute("errors", new Binding());
        model.addAttribute("referrer", referrer);
        model.addAttribute(DATAMODEL, knight);
        return "edit-stormknight";
    }

    @PostMapping("/")
    @RolesAllowed("PLAYER")
    public String saveOperative(
        @NotNull Principal principal,
        @Valid @NotNull @ModelAttribute(DATAMODEL) Operative knight,
        BindingResult binding,
        @NotNull Model model
    ) {
        log.info("Saving storm knight data. user={}, knight={}", principal.getName(), knight);

        model.addAttribute("errors", binding);
        if (binding.hasErrors() && binding.getAllErrors().size() > 1) {
            log.warn("Data is invalid. knight={}, errors={}", knight, binding.getAllErrors());

            model.addAttribute("cosms", Cosm.values());
            model.addAttribute("referrer", "/" + principal.getName() + "/list");
            model.addAttribute(DATAMODEL, knight);
            return "edit-stormknight";
        }

        if (
            ! principal.getName().equals(knight.getNameSpace())
            && !hasRole(principal, Set.of("ROLE_ORGA","ROLE_ADMIN"))
        ) {
            log.warn("The storm knight is not owned by the user. user={}, knight={}", principal, knight);
        } else {
            knight = protectKnightData(knight, principal);
            knight = stormKnightRepository.save(knight);

            log.info("Changed storm knight saved. knight={}", knight);
        }

        model.addAttribute(DATAMODEL, knight);

        return "redirect:/" + principal.getName() + "/list";
    }

    private boolean hasRole(Principal user, Set<String> roles) {
        log.info("Checking roles. principal={}", user);

        return roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r)));
    }

    private Operative protectKnightData(final Operative knight, final Principal user) {
        if (hasRole(user, Set.of("ROLE_ORGA", "ROLE_ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return knight;
        }

        Operative orig = stormKnightRepository.findByUid(knight.getUid());
        if (orig == null) return knight;

        return orig.toBuilder()
                .name(knight.getName())
                .build();
    }
}
