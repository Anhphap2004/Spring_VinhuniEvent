package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.service.EventService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin/event_register")
public class EventRegisterController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;

    public EventRegisterController(EventService eventService, EventRegistrationService registrationService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    /**
     * CHỨC NĂNG 1: HIỂN THỊ DANH SÁCH SỰ KIỆN VÀ THỐNG KÊ ĐĂNG KÝ
     * GET /admin/event_register
     */
    @GetMapping
    public String listRegisteredEvents(Model model) {
        // Gọi Service đã được tối ưu hóa (JOIN FETCH)
        List<Event> events = eventService.getAllEvents();

        model.addAttribute("events", events);
        // Tên file View: src/main/resources/templates/admin/event_register/index.html
        return "admin/event_register/list";
    }

    /**
     * CHỨC NĂNG 2: XEM CHI TIẾT DANH SÁCH NGƯỜI DÙNG ĐÃ ĐĂNG KÝ
     * GET /admin/event_register/{eventId}
     */
    @GetMapping("/{eventId}")
    public String detailRegistrations(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Sự kiện không tồn tại."));

            // Lấy danh sách người dùng đã đăng ký (Sử dụng FETCH EAGERLY User)
            List<EventRegistration> registrations = registrationService.getRegistrationsByEventId(eventId);

            model.addAttribute("event", event);
            model.addAttribute("registrations", registrations);
            return "admin/event_register/detail";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/event_register";
        }
    }

    /**
     * CHỨC NĂNG 3: XÁC NHẬN/CẬP NHẬT TRẠNG THÁI ĐĂNG KÝ
     * POST /admin/event_register/update/{registrationId}
     */
    @PostMapping("/update/{registrationId}")
    public String updateRegistrationStatus(@PathVariable Long registrationId,
                                           @RequestParam("status") String newStatus,
                                           RedirectAttributes redirectAttributes) {
        Long eventId = null;
        try {
            EventRegistration updatedReg = registrationService.updateRegistrationStatus(registrationId, newStatus);

            eventId = updatedReg.getEvent().getEvent_id();

            redirectAttributes.addFlashAttribute("successMessage",
                    "Cập nhật trạng thái đăng ký #" + registrationId + " thành công (" + newStatus + ").");

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Chuyển hướng về trang chi tiết sự kiện
        return "redirect:/admin/event_register/" + eventId;
    }
}