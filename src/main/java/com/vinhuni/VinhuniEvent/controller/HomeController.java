package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import org.springframework.ui.Model;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("")
public class HomeController {
    private final EventService eventService;
    private final EventCategoryService eventCategoryService;
    public HomeController(EventService eventService,  EventCategoryService eventCategoryService) {
        this.eventService = eventService;
        this.eventCategoryService = eventCategoryService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("eventCategories", eventCategoryService.getAllEventCategories());
        return "main/home";
    }
    @GetMapping("/filter-events")
    public String filterEvents(
            @RequestParam(value = "category", required = false) Long categoryId,
            Model model
    ) {
        List<Event> events;
        if (categoryId == null || categoryId.equals(0L)) {
            events = eventService.getAllEvents();
        } else {
            events = eventService.getEventsByCategoryId(categoryId);
        }
        model.addAttribute("events", events);
        return "fragments/event-list :: eventListFragment";
    }



}
