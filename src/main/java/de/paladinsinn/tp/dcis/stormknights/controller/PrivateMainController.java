package de.paladinsinn.tp.dcis.stormknights.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.security.RolesAllowed;


@Controller
@RequestMapping("/dcis/")
public class PrivateMainController {

    @GetMapping
    @RolesAllowed({"ADMIN", "ORGA", "JUDGE", "GM", "PLAYER"})
    public String getMethodName(Model model) {
        model.addAttribute("access_level", "Spieler");

        return "main";
    }
    
}
