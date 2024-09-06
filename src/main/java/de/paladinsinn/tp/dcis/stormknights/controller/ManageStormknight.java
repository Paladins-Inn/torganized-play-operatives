package de.paladinsinn.tp.dcis.stormknights.controller;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.paladinsinn.tp.dcis.stormknights.domain.model.StormKnight;
import de.paladinsinn.tp.dcis.stormknights.domain.service.StormKnightRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ManageStormknight {
    private final StormKnightRepository stormKnightRepository;

    @GetMapping("/")
    @RolesAllowed("PLAYER")
    public String createStormKnight(
        @NotNull Principal principal,
        @ModelAttribute("knight") StormKnight knight,
        BindingResult binding,
        @NotNull Model model
    ) {

        log.info("Showing input form for new storm knights. user={}", 
            principal.getName()
        );

        knight = StormKnight.builder()
            .uid(UUID.randomUUID())
            .nameSpace(principal.getName())
            .build();

        model.addAttribute("errors", binding);
        model.addAttribute("knight", knight);
        return "edit-stormknight";
    }


    @PostMapping("/")
    @RolesAllowed("PLAYER")
    public String saveStormKnight(
        @NotNull Principal user,
        @Valid @NotNull @ModelAttribute("knight") StormKnight knight,
        BindingResult binding,
        @NotNull Model model
    ) {
        log.info("Saving storm knight data. user={}, knight={}", user.getName(), knight);

        model.addAttribute("errors", binding);
        if (binding.hasErrors()) {
            model.addAttribute("knight", knight);
            return "edit-stormknight";
        }

        if (
            ! user.getName().equals(knight.getNameSpace())
            && !hasRole(user, Set.of("ROLE_ORGA","ROLE_ADMIN"))
        ) {
            log.warn("The storm knight is not owned by the user. user={}, knight={}", user, knight);
        } else {
            knight = protectKnightData(knight, user);
            knight = stormKnightRepository.save(knight);

            log.info("Changed storm knight saved. knight={}", knight);
        }

        model.addAttribute("knight", knight);

        return "redirect:/" + user.getName() + "/list";
    }

    private boolean hasRole(Principal user, Set<String> roles) {
        log.info("Checking roles. principal={}", user);

        return roles.stream().anyMatch(r -> ((OAuth2AuthenticationToken)user).getAuthorities().contains(new SimpleGrantedAuthority(r)));
    }

    private StormKnight protectKnightData(final StormKnight knight, final Principal user) {
        if (hasRole(user, Set.of("ROLE_ORGA", "ROLE_ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return knight;
        }

        StormKnight orig = stormKnightRepository.findByUid(knight.getUid());
        if (orig == null) return knight;

        return orig.toBuilder()
                .name(knight.getName())
                .build();
    }
}
