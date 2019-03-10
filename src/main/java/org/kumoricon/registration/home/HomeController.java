package org.kumoricon.registration.home;

import org.kumoricon.registration.model.badge.Badge;
import org.kumoricon.registration.helpers.AuthHelper;
import org.kumoricon.registration.model.badge.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private final BadgeService badgeService;

    @Autowired
    public HomeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @RequestMapping(value = "/")
    public String home(final Model model, final Authentication authentication) {

        List<Badge> badges = new ArrayList<>();
        for (Badge b : badgeService.findByVisibleTrue()) {
            if (AuthHelper.userHasAuthority(authentication, b.getRequiredRight()))
                badges.add(b);
        }
        model.addAttribute("badges", badges);
        return "home";
    }

}
