package com.umg.guali.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String raiz() {
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/panel")
    public String panelAdmin(Authentication authentication, Model model) {
        model.addAttribute("nombreAdmin", authentication.getName());
        return "admin_panel";
    }
}