package de.paladinsinn.tp.dcis.operatives.controller;

import de.paladinsinn.tp.dcis.operatives.persistence.OperativeRepository;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.paladinsinn.tp.dcis.operatives.persistence.OperativeJPA;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@XSlf4j
@Controller
@RequestMapping("/")
public class ListOperatives {
    private final OperativeRepository operativesRepository;

    @Value("${server.servlet.contextPath}:/operatives")
    private String contextPath;

    @GetMapping("public/list")
    @PermitAll
    public String publicList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        log.entry(size, page, model);
        prepareListOfOperatives("public/list", size, page, model);

        return log.exit("list-operatives");
    }

    private void prepareListOfOperatives(final String url, final int size, final int page, Model model) {
        log.entry(url, size, page, model);

        Pageable p = Pageable.ofSize(size).withPage(page);

        Page<OperativeJPA> knights = operativesRepository.findAll(p);
        log.info("Storm knights list loaded. page={}, size={}, noOfOperatives={}", page, size, knights.getTotalElements());

        model.addAttribute("contextPath", contextPath);
        model.addAttribute("url", url);
        model.addAttribute("operatives", knights);
        log.info("Listing operatives. knights={}", knights.getContent());
        log.exit();
    }

    @GetMapping("orga/list")
    @RolesAllowed("ORGA")
    public String orgaList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        log.entry(size, page, model);

        prepareListOfOperatives("orga/list", size, page, model);

        return log.exit("list-operatives");
    }

    @GetMapping("judge/list")
    @RolesAllowed("ORGA")
    public String judgeList(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        log.entry(size, page, model);

        prepareListOfOperatives("judge/list", size, page, model);

        return log.exit("list-operatives");
    }

    @GetMapping("{name}/list")
    public String playerList(
        final String name, 
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "0") final int page,
        Model model
    ) {
        log.entry(name, size, page, model);

        prepareListOfOperatives(name + "/list", size, page, model);

        return log.exit("list-operatives");
    }
}
