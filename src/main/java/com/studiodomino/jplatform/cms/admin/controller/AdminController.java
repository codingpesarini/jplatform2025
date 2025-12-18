package com.studiodomino.jplatform.cms.admin.controller;

import com.studiodomino.jplatform.shared.config.ConfigurazioneCore;
import com.studiodomino.jplatform.shared.service.ConfigurationService;
import com.studiodomino.jplatform.shared.util.ViewUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ConfigurationService configurationService;

    @GetMapping
    public String dashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        // ✅ OTTIENE ConfigurazioneCore
        ConfigurazioneCore configCore = configurationService.getConfig(session);

        // Verifica login
        if (!configCore.isLogged()) {
            return "redirect:/login";
        }

        log.info("=== ADMIN DASHBOARD === user: {}", configCore.getUsername());

        // ✅ PASSA ConfigurazioneCore al template
        model.addAttribute("config", configCore);

        return ViewUtils.resolveProtectedTemplate("front/dashboard");
    }
}