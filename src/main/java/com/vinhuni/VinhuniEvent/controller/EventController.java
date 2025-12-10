package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Spring tự inject ServiceImpl vào đây
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 1) Hiển thị danh sách Event
    @GetMapping
    public String listEvents(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "main/event/index"; // tên file .html
    }

    // 2) Xem chi tiết 1 Event
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var event = eventService.getEventById(id).orElse(null);
        model.addAttribute("event", event);
        return "event/detail";
    }

    // 3) Form tạo Event
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("event", new Event());
        return "event/create";
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
