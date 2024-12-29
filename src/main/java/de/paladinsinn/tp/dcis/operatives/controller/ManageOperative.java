package de.paladinsinn.tp.dcis.operatives.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
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
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;
import de.paladinsinn.tp.dcis.operatives.persistence.OperativeRepository;
import groovy.lang.Binding;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@XSlf4j
@Controller
@RequestMapping("/")
public class ManageOperative {
    private static final String DATAMODEL = "knight";

    private final OperativeRepository operativesRepository;

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

        if (referrer == null) {
            referrer = "/" + principal.getName() + "/list";
        }

        Operative knight = OperativeJPA.builder()
            .id(UUID.randomUUID())
            .nameSpace(principal.getName())
            .build();

        model.addAttribute("contextPath", contextPath);
        model.addAttribute("cosms", Cosm.values());
        model.addAttribute("errors", new Binding());
        model.addAttribute("referrer", referrer);
        model.addAttribute(DATAMODEL, knight);

        return log.exit("edit-operatives");
    }

    @PostMapping("/")
    @RolesAllowed("PLAYER")
    public String saveOperative(
        @NotNull Principal principal,
        @Valid @NotNull @ModelAttribute(DATAMODEL) OperativeJPA knight,
        BindingResult binding,
        @NotNull Model model
    ) {
        log.entry(principal, knight, binding, model);

        log.info("Saving operative data. user={}, knight={}", principal.getName(), knight);

        model.addAttribute("errors", binding);
        if (binding.hasErrors() && binding.getAllErrors().size() > 1) {
            log.warn("Data is invalid. knight={}, errors={}", knight, binding.getAllErrors());

            model.addAttribute("contextPath", contextPath);
            model.addAttribute("cosms", Cosm.values());
            model.addAttribute("referrer", "/" + principal.getName() + "/list");
            model.addAttribute(DATAMODEL, knight);
            return "edit-operatives";
        }

        if (
            ! principal.getName().equals(knight.getNameSpace())
            && !hasRole(principal, Set.of("ROLE_ORGA","ROLE_ADMIN"))
        ) {
            log.warn("The operative is not owned by the user. user={}, knight={}", principal, knight);
        } else {
            knight = protectKnightData(knight, principal);
            knight = operativesRepository.save(knight);

            log.info("Changed operative saved. knight={}", knight);
        }

        model.addAttribute(DATAMODEL, knight);

        return log.exit("redirect:/" + principal.getName() + "/list");
    }

    private boolean hasRole(Principal user, Set<String> roles) {
        log.entry(user, roles);

        log.info("Checking roles. principal={}", user);

        return log.exit(roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r))));
    }

    private OperativeJPA protectKnightData(final OperativeJPA knight, final Principal user) {
        log.entry(knight, user);

        if (hasRole(user, Set.of("ROLE_ORGA", "ROLE_ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return log.exit(knight);
        }

        Optional<OperativeJPA> orig = operativesRepository.findById(knight.getId());
        if (orig.isEmpty()) {
            return log.exit(null);
        }

        return log.exit(orig.get().toBuilder()
                .name(knight.getName())
                .build()
        );
    }
}
