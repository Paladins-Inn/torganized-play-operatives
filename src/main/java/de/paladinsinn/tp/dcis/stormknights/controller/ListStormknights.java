package de.paladinsinn.tp.dcis.stormknights.controller;

import java.util.LinkedList;
import java.util.List;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/")
public class ListStormknights {
    private final StormKnightRepository stormKnightRepository;

    @GetMapping("list")
    @PermitAll
    public String getMethodName(
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "1") final int page,
        Model model
    ) {
        Pageable p = Pageable.ofSize(size).withPage(page);
        Page<StormKnight> knights = stormKnightRepository.findAll(p);
        log.info("Storm knights list loaded. page={}, size={}, noOfStormKnights={}", page, size, knights.getTotalElements());

        model.addAttribute("url", "list");
        model.addAttribute("page", knights.getPageable());
        model.addAttribute("stormknights", knights.getContent());
        model.addAttribute("no_of_knights", knights.getTotalElements());

        return "list-stormknights";
    }

    @GetMapping("{name}/list")
    public String getStormKnights(
        final String name, 
        @RequestParam(defaultValue = "50") final int size,
        @RequestParam(defaultValue = "1") final int page,
        Model model
    ) {
        Pageable p = Pageable.ofSize(size).withPage(page);
        Page<StormKnight> knights = stormKnightRepository.findByNamespace(name, p);
        log.info("Storm knights list loaded. page={}, size={}, noOfStormKnights={}", page, size, knights.getTotalElements());

        model.addAttribute("url", "list");
        model.addAttribute("page", knights.getPageable());
        model.addAttribute("stormknights", knights.getContent());
        model.addAttribute("no_of_knights", knights.getTotalElements());

        return "list-stormknights";
    }
}
