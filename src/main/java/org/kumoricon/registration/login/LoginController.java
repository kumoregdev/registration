package org.kumoricon.registration.login;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
class loginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginGet(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "logout", required = false) String logout) {

        ModelAndView m = new ModelAndView();
        m.setViewName("login");
        return m;
    }

    @RequestMapping(value = "/confirmlogout", method = RequestMethod.GET)
    public ModelAndView confirmLogout() {

        ModelAndView m = new ModelAndView();
        m.setViewName("confirmlogout");
        return m;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        ModelAndView m = new ModelAndView();
        m.setViewName("login?logout");
        return m;
    }
}

