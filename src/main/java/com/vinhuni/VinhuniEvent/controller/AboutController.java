package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.About;
import com.vinhuni.VinhuniEvent.service.AboutEventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AboutController {


    private final AboutEventService aboutService;


    public AboutController(AboutEventService aboutService) {
        this.aboutService = aboutService;
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {


        List<About> aboutList = aboutService.getAllEvents();


        model.addAttribute("aboutList", aboutList);

        return "main/about/index";
    }
}