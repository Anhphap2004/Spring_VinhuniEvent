package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventCategoryService eventCategoryService;
    // Spring tự inject ServiceImpl vào đây
    public EventController(EventService eventService,  EventCategoryService eventCategoryService) {
        this.eventService = eventService;
        this.eventCategoryService = eventCategoryService;
    }

    // 1) Hiển thị danh sách Event
    @GetMapping
    public String listEvents(
            @RequestParam(value = "category", required = false) Long categoryId,
            Model model) {

        List<Event> events;

        if (categoryId != null) {
            events = eventService.getEventsByCategoryId(categoryId);
        } else {
            events = eventService.getAllEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("eventCategories", eventCategoryService.getAllEventCategories());
        model.addAttribute("selectedCategory", categoryId);

        return "main/event/index";
    }

    // 2) Xem chi tiết 1 Event
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var event = eventService.getEventById(id).orElse(null);
        model.addAttribute("event", event);
        return "main/event/detail";
    }

    // 3) Form tạo Event
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("eventCategories", eventCategoryService.getAllEventCategories());
        return "main/event/create";
    }

    // 4) Lưu Event
    @PostMapping("/create")
    public String save(@ModelAttribute Event event) {
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    // 5) Xoá Event
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }
}
