package com.vinhuni.VinhuniEvent.controller;

import org.springframework.ui.Model;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("")
public class HomeController {
    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("events", eventService.getAllEvents());
        return "main/home";
    }
}
