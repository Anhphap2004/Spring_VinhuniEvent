package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.config.RequiredRole;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller// Chỉ cho phép User (roleId = 1) truy cập
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
    public String listEvents(@RequestParam(value = "category", required = false) Long categoryId, Model model) {
        List<Event> events;

        if (categoryId != null) {
            events = eventService.getActiveEventsByCategoryId(categoryId);
        } else {
            events = eventService.getAllActiveEvents();
        }

        // --- BỔ SUNG 2 DÒNG NÀY ---
        // Lấy danh sách danh mục để hiển thị lên sidebar
        model.addAttribute("eventCategories", eventCategoryService.getAllEventCategories());
        // Truyền categoryId hiện tại để làm sáng (active) menu bên trái
        model.addAttribute("selectedCategory", categoryId);
        // --------------------------

        model.addAttribute("events", events);
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

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime endTime = event.getEndTime();
        boolean hasStarted = startTime != null && !startTime.isAfter(now);
        boolean hasEnded = endTime != null && !endTime.isAfter(now);
        long startCountdownSeconds = (startTime != null && startTime.isAfter(now))
                ? Duration.between(now, startTime).getSeconds()
                : 0;

        model.addAttribute("hasStarted", hasStarted);
        model.addAttribute("hasEnded", hasEnded);
        model.addAttribute("startCountdownSeconds", startCountdownSeconds);

        // Mặc định là chưa đăng ký
        boolean isRegistered = false;
        String registrationStatus = "";

        if (loggedInUser != null) {
            Long currentUserId = loggedInUser.getUserId();
            model.addAttribute("currentUserId", currentUserId);

            // LOGIC MỚI: Tìm bản ghi đăng ký để lấy trạng thái cụ thể
            Optional<EventRegistration> registrationOpt = registrationService.findRegistration(id, currentUserId);

            if (registrationOpt.isPresent()) {
                isRegistered = true;
                registrationStatus = registrationOpt.get().getStatus(); // Lấy "Đã đăng ký" hoặc "Đã xác nhận đăng ký"
            }
        } else {
            model.addAttribute("currentUserId", null);
        }

        // Truyền biến sang View
        model.addAttribute("isRegistered", isRegistered);
        model.addAttribute("registrationStatus", registrationStatus);

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

        var eventOptional = eventService.getEventById(eventId);
        if (eventOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sự kiện không tồn tại.");
            return "redirect:/events";
        }

        Event event = eventOptional.get();
        LocalDateTime now = LocalDateTime.now();
        if (event.getEndTime() != null && !event.getEndTime().isAfter(now)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sự kiện đã kết thúc, không thể đăng ký.");
            return "redirect:/events/" + eventId;
        }
        if (event.getStartTime() != null && !event.getStartTime().isAfter(now)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sự kiện đã bắt đầu, không thể đăng ký thêm.");
            return "redirect:/events/" + eventId;
        }

        Long currentUserId = loggedInUser.getUserId();

        try {
            registrationService.registerForEvent(eventId, currentUserId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã đăng ký thành công, vui lòng chờ duyệt!");

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/events/" + eventId;
    }

    // Các method khác giữ nguyên
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

