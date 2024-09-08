package de.paladinsinn.tp.dcis.stormknights.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.paladinsinn.tp.dcis.stormknights.domain.model.StormKnight;
import de.paladinsinn.tp.dcis.stormknights.domain.service.StormKnightRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ListStormKnights {
    private final StormKnightRepository stormKnightRepository;

    @GetMapping("public/list")
    @PermitAll
    public String publicList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfStormKnights("public/list", size, page, model);

        return "list-stormknights";
    }

    private void prepareListOfStormKnights(final String url, final int size, final int page, Model model) {
        Pageable p = Pageable.ofSize(size).withPage(page);

        Page<StormKnight> knights = stormKnightRepository.findAll(p);
        log.info("Storm knights list loaded. page={}, size={}, noOfStormKnights={}", page, size, knights.getTotalElements());

        model.addAttribute("url", url);
        model.addAttribute("stormknights", knights);
        log.info("Listing stormknights. knights={}", knights.getContent());
    }

    @GetMapping("orga/list")
    @RolesAllowed("ORGA")
    public String orgaList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfStormKnights("orga/list", size, page, model);

        return "list-stormknights";
    }

    @GetMapping("judge/list")
    @RolesAllowed("ORGA")
    public String judgeList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfStormKnights("judge/list", size, page, model);

        return "list-stormknights";
    }

    @GetMapping("{name}/list")
    public String playerList(
        final String name, 
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfStormKnights(name + "/list", size, page, model);

        return "list-stormknights";
    }
}