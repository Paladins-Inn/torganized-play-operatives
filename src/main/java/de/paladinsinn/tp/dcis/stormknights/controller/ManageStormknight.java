package de.paladinsinn.tp.dcis.stormknights.controller;

import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.paladinsinn.tp.dcis.stormknights.domain.model.StormKnight;
import de.paladinsinn.tp.dcis.stormknights.domain.service.StormKnightRepository;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ManageStormknight {
    private final StormKnightRepository stormKnightRepository;

    @PostMapping()
    @RolesAllowed("PLAYER")
    public String createStormKnight(
        @AuthenticationPrincipal UserPrincipal user,
        Model model
    ) {
        log.info("Registering a new storm knight. user={}", user);
        StormKnight knight = StormKnight.builder()
                .nameSpace(user.getName())
                .build();

        model.addAttribute("knight", knight);

        return "edit-stormknight";
    }

    @PostMapping()
    @RolesAllowed("PLAYER")
    public String saveStormKnight(
        @AuthenticationPrincipal UserPrincipal user,
        @ModelAttribute StormKnight knight,
        Model model
    ) {
        log.info("Saving storm knight data. user={}, knight={}", user, knight);

        if (
            ! knight.getNameSpace().equals(user.getName())
            && !hasRole(user, Set.of("ORGA","ADMIN"))
        ) {
            log.warn("The storm knight is not owned by the user. user={}, knight={}", user, knight);
        } else {
            knight = protectKnightData(knight, user);
            knight = stormKnightRepository.save(knight);

            log.info("Changed storm knight saved. knight={}", knight);
        }

        model.addAttribute("knight", knight);

        return "edit-stormknight";
    }

    private boolean hasRole(UserPrincipal user, Set<String> roles) {
        return roles.stream().anyMatch(r -> { return ((UserDetails) user).getAuthorities().contains(r) ;});
    }

    private StormKnight protectKnightData(final StormKnight knight, final UserPrincipal user) {
        if (hasRole(user, Set.of("ORGA", "ADMIN"))) {
            log.debug("Admins and orga may change anything.");

            return knight;
        }

        StormKnight orig = stormKnightRepository.findByUid(knight.getUid());
        return orig.toBuilder()
                .name(knight.getName())
                .build();
    }
}
