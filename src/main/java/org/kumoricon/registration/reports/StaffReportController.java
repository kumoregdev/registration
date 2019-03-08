package org.kumoricon.registration.reports;

//import org.kumoricon.registration.model.user.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
public class StaffReportController {
//    private final StaffRepository staffRepository;

//    @Autowired
//    public StaffReportController(StaffRepository staffRepository) {
//        this.staffRepository = staffRepository;
//    }
    public StaffReportController() {
//        this.staffRepository = null;
    }

    @RequestMapping(value = "/reports/staff")
    @PreAuthorize("hasAuthority('view_staff_report')")
    public String staff(Model model,
                        @RequestParam(required=false) String err,
                        @RequestParam(required=false) String msg) {
        try {
//            List<Object> users = staffRepository.findAllStaff();
            model.addAttribute("staff", new ArrayList<>());
            model.addAttribute("err", err);
        } catch (NumberFormatException ex) {
            model.addAttribute("err", ex.getMessage());
        }

        model.addAttribute("msg", msg);

        return "reports/staff";
    }
}