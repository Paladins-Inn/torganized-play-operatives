/*
 * Copyright (c) 2024. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or  (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

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


/**
 * Lists the operatives for the different roles using the DCIS.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.1.0-SNAPSHOT
 * @version 1.1.0-SNAPSHOT
 */
@RequiredArgsConstructor
@XSlf4j
@Controller
@RequestMapping("/")
public class ListOperatives {
    /**
     * The data model name for the list of operatives.
     */
    public static final String DATAMODEL = "operatives";

    /**
     * The repository to access the operatives.
     */
    private final OperativeRepository operativesRepository;

    /**
     * The context path of the application.
     */
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

    /**
     * Prepares the data for the list of operatives.
     *
     * @param url The URL to call for the next page.
     * @param size The size of the list.
     * @param page The displayed page number.
     * @param model The data model to fill with the data.
     */
    private void prepareListOfOperatives(final String url, final int size, final int page, Model model) {
        log.entry(url, size, page, model);

        Pageable p = Pageable.ofSize(size).withPage(page);

        Page<OperativeJPA> operatives = operativesRepository.findAll(p);
        log.info("Operatives list loaded. page={}, size={}, noOfOperatives={}", page, size, operatives.getTotalElements());

        model.addAttribute("contextPath", contextPath);
        model.addAttribute("url", url);
        model.addAttribute(DATAMODEL, operatives);
        log.info("Listing operatives. operatives={}", operatives.getContent());
        log.exit();
    }

    @GetMapping("orga/list")
    @RolesAllowed({"ORGA", "ADMIN"})
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
    @RolesAllowed({"ORGA", "JUDGE", "ADMIN"})
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
    @RolesAllowed({"PLAYER", "ORGA", "ADMIN"})
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
