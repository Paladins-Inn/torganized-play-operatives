package de.paladinsinn.tp.dcis.operatives.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.paladinsinn.tp.dcis.operatives.domain.model.Operative;
import de.paladinsinn.tp.dcis.operatives.domain.service.OperativeRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ListOperatives {
    private final OperativeRepository stormKnightRepository;

    @GetMapping("public/list")
    @PermitAll
    public String publicList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfOperatives("public/list", size, page, model);

        return "list-operatives";
    }

    private void prepareListOfOperatives(final String url, final int size, final int page, Model model) {
        Pageable p = Pageable.ofSize(size).withPage(page);

        Page<Operative> knights = stormKnightRepository.findAll(p);
        log.info("Storm knights list loaded. page={}, size={}, noOfOperatives={}", page, size, knights.getTotalElements());

        model.addAttribute("url", url);
        model.addAttribute("operatives", knights);
        log.info("Listing operatives. knights={}", knights.getContent());
    }

    @GetMapping("orga/list")
    @RolesAllowed("ORGA")
    public String orgaList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfOperatives("orga/list", size, page, model);

        return "list-operatives";
    }

    @GetMapping("judge/list")
    @RolesAllowed("ORGA")
    public String judgeList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfOperatives("judge/list", size, page, model);

        return "list-operatives";
    }

    @GetMapping("{name}/list")
    public String playerList(
        final String name, 
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        prepareListOfOperatives(name + "/list", size, page, model);

        return "list-operatives";
    }
}
