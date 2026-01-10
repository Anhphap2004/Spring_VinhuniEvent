package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.config.RequiredRole;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.service.AttendanceService;
import com.vinhuni.VinhuniEvent.service.EventService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequiredRole({1, 3})
@RequestMapping("/admin/event_register")
public class EventRegisterController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final AttendanceService attendanceService;


    public EventRegisterController(
            EventService eventService,
            EventRegistrationService registrationService,
            AttendanceService attendanceService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
    }


    @GetMapping
    public String listRegisteredEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "admin/event_register/list";
    }


    @GetMapping("/{eventId}")
    public String detailRegistrations(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Sự kiện không tồn tại."));


            List<EventRegistration> registrations = registrationService.getRegistrationsByEventId(eventId);

            model.addAttribute("event", event);
            model.addAttribute("registrations", registrations);
            return "admin/event_register/detail";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/event_register";
        }
    }


    @PostMapping("/update/{registrationId}")
    public String updateRegistrationStatus(@PathVariable Long registrationId,
                                           @RequestParam("action") String action,
                                           @RequestParam(value = "eventId", required = false) Long eventId,
                                           RedirectAttributes redirectAttributes) {
        try {
            if ("confirm".equals(action)) {

                registrationService.updateRegistrationStatus(registrationId, "Đã xác nhận đăng ký");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã xác nhận đăng ký thành công.");
            }
            else if ("cancel".equals(action)) {

                registrationService.deleteRegistration(registrationId);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã hủy và xóa bản ghi đăng ký thành công.");
            }

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }


        return (eventId != null) ? "redirect:/admin/event_register/" + eventId : "redirect:/admin/event_register";
    }


    @GetMapping("/{eventId}/attendance")
    public String showAttendancePage(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Sự kiện không tồn tại."));

            List<EventRegistration> registrations = registrationService.getRegistrationsByEventId(eventId);


            long totalPresent = 0;
            long totalAbsent = 0;

            long validRegistrationsCount = registrations.size();

            Map<Long, Boolean> attendanceStatusMap = new HashMap<>();

            for (EventRegistration reg : registrations) {
                Long userId = reg.getUser().getUser_id();
                Optional<Boolean> status = attendanceService.getUserAttendanceStatus(eventId, userId);

                if (status.isPresent()) {
                    attendanceStatusMap.put(userId, status.get());
                    if (status.get()) {
                        totalPresent++;
                    } else {
                        totalAbsent++;
                    }
                }
            }


            long totalPendingAttendance = validRegistrationsCount - totalPresent - totalAbsent;

            model.addAttribute("totalRegistrations", validRegistrationsCount);
            model.addAttribute("totalPresent", totalPresent);
            model.addAttribute("totalAbsent", totalAbsent);
            model.addAttribute("totalPendingAttendance", totalPendingAttendance);

            model.addAttribute("event", event);
            model.addAttribute("registrations", registrations);
            model.addAttribute("attendanceStatusMap", attendanceStatusMap);

            return "admin/event_register/attendance";

        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/event_register";
        }
    }


    @PostMapping("/attendance/record")
    public String recordStudentAttendance(
            @RequestParam("eventId") Long eventId,
            @RequestParam("userId") Long userId,
            @RequestParam("isPresent") boolean isPresent,
            RedirectAttributes redirectAttributes) {

        try {
            attendanceService.recordAttendance(eventId, userId, isPresent);

            String statusText = isPresent ? "Có mặt" : "Vắng mặt";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã điểm danh " + statusText + " cho sinh viên (ID: " + userId + ").");

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/event_register/" + eventId + "/attendance";
    }
}