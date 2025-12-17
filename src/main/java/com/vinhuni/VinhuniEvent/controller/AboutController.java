package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.SchoolInfo;
import com.vinhuni.VinhuniEvent.service.SchoolInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    private SchoolInfoService schoolInfoService;
    public AboutController(SchoolInfoService schoolInfoService) {
        this.schoolInfoService = schoolInfoService;
    }
    @GetMapping("/about")
    public String about(Model model) {
        SchoolInfo school = schoolInfoService.getSchoolInfo();
        model.addAttribute("school", school);
        model.addAttribute("pageTitle", "Giới thiệu về " + school.getSchoolName());
        return "main/about";
    }
}
