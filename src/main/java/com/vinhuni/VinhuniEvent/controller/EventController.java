package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.NoSuchElementException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventCategoryService eventCategoryService;
    private final EventRegistrationService registrationService;

    public EventController(
            EventService eventService,
            EventCategoryService eventCategoryService,
            EventRegistrationService registrationService) {
        this.eventService = eventService;
        this.eventCategoryService = eventCategoryService;
        this.registrationService = registrationService;
    }

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

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        var eventOptional = eventService.getEventById(id);

        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Sự kiện có ID " + id + " không tồn tại.");
            return "redirect:/events";
        }

        Event event = eventOptional.get();
        model.addAttribute("event", event);

        if (loggedInUser == null) {
            model.addAttribute("isRegistered", false);
            model.addAttribute("currentUserId", null);
            return "main/event/detail";
        }

        Long currentUserId = loggedInUser.getUser_id();
        model.addAttribute("currentUserId", currentUserId);
        try {
            registrationService.checkIfRegistered(id, currentUserId);
            model.addAttribute("isRegistered", true);
        } catch (RegistrationException e) {
            model.addAttribute("isRegistered", false);
        }

        return "main/event/detail";
    }

    @PostMapping("/{id}/register")
    public String registerEvent(@PathVariable("id") Long eventId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Vui lòng đăng nhập để đăng ký sự kiện.");
            return "redirect:/login";
        }

        Long currentUserId = loggedInUser.getUser_id();

        try {
            registrationService.registerForEvent(eventId, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bạn đã đăng ký sự kiện thành công!");

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/events/" + eventId;
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("eventCategories", eventCategoryService.getAllEventCategories());
        return "main/event/create";
    }

    @PostMapping("/create")
    public String save(@ModelAttribute Event event) {
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }
}