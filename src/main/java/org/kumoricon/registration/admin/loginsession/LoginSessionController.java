package org.kumoricon.registration.admin.loginsession;

import org.kumoricon.registration.model.loginsession.LoginRepository;
import org.kumoricon.registration.model.loginsession.SessionInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LoginSessionController {
    private final LoginRepository loginRepository;

    @Autowired
    public LoginSessionController(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    @RequestMapping(value = "/admin/loginsessions")
    @PreAuthorize("hasAuthority('view_active_sessions')")
    public String admin(Model model) {
        List<SessionInfoDTO> logins = loginRepository.findAll();

        model.addAttribute("loginSessions", logins);
        return "admin/loginsessions";
    }
}
