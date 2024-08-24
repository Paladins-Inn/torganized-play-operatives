package de.paladinsinn.tp.dcis.stormknights.controller;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String getMethodName(Model model) {
        List<StormKnight> knights = stormKnightRepository.findAll();
        log.info("Storm knights list loaded. noOfStormKnights={}", knights.size());

        model.addAttribute("stormknights", knights);
        model.addAttribute("no_of_knights", knights.size());

        return "list-stormknights";
    }

    @GetMapping("{name}/list")
    public String getStormKnights(final String name, Model model) {
        List<StormKnight> knights = stormKnightRepository.findByNameSpace(name);

        model.addAttribute("stormknights", knights);
        model.addAttribute("no_of_knights", knights.size());

        return "list-stormknights";
    }
}
