package com.vinhuni.VinhuniEvent.controller;

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
@RequestMapping("/admin/event_register")
public class EventRegisterController {

    private final EventService eventService;
    private final EventRegistrationService registrationService;
    private final AttendanceService attendanceService;

    // Constructor Injection
    public EventRegisterController(
            EventService eventService,
            EventRegistrationService registrationService,
            AttendanceService attendanceService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
    }

    // ========================================================================
    // CHỨC NĂNG QUẢN LÝ ĐĂNG KÝ (LIST & DETAIL)
    // ========================================================================

    /**
     * 1. HIỂN THỊ DANH SÁCH SỰ KIỆN
     * GET /admin/event_register
     */
    @GetMapping
    public String listRegisteredEvents(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "admin/event_register/list";
    }

    /**
     * 2. XEM CHI TIẾT DANH SÁCH NGƯỜI DÙNG ĐÃ ĐĂNG KÝ
     * GET /admin/event_register/{eventId}
     */
    @GetMapping("/{eventId}")
    public String detailRegistrations(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Sự kiện không tồn tại."));

            // Lấy danh sách đăng ký hiện có trong DB
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
     * 3. XỬ LÝ HÀNH ĐỘNG: XÁC NHẬN HOẶC HỦY (XÓA)
     * POST /admin/event_register/update/{registrationId}
     * * @param action: Nhận giá trị "confirm" hoặc "cancel" từ nút bấm ở View
     */
    @PostMapping("/update/{registrationId}")
    public String updateRegistrationStatus(@PathVariable Long registrationId,
                                           @RequestParam("action") String action,
                                           @RequestParam(value = "eventId", required = false) Long eventId,
                                           RedirectAttributes redirectAttributes) {
        try {
            if ("confirm".equals(action)) {
                // LOGIC 1: Xác nhận -> Đổi status thành "Đã xác nhận đăng ký"
                registrationService.updateRegistrationStatus(registrationId, "Đã xác nhận đăng ký");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã xác nhận đăng ký thành công.");
            }
            else if ("cancel".equals(action)) {
                // LOGIC 2: Hủy -> Xóa bản ghi khỏi Database
                registrationService.deleteRegistration(registrationId);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã hủy và xóa bản ghi đăng ký thành công.");
            }

        } catch (RegistrationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Quay lại trang chi tiết sự kiện
        return (eventId != null) ? "redirect:/admin/event_register/" + eventId : "redirect:/admin/event_register";
    }

    // ========================================================================
    // CHỨC NĂNG ĐIỂM DANH (ATTENDANCE)
    // ========================================================================

    /**
     * 4. TRANG QUẢN LÝ ĐIỂM DANH (THỐNG KÊ & DANH SÁCH)
     * GET /admin/event_register/{eventId}/attendance
     */
    @GetMapping("/{eventId}/attendance")
    public String showAttendancePage(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Event event = eventService.getEventById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Sự kiện không tồn tại."));

            List<EventRegistration> registrations = registrationService.getRegistrationsByEventId(eventId);

            // --- KHỐI THỐNG KÊ ---
            long totalPresent = 0;
            long totalAbsent = 0;
            // Lưu ý: Vì Hủy là Xóa, nên danh sách registrations chỉ chứa những người còn hiệu lực (Đã đăng ký/Đã xác nhận)
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

            // Những người chưa được điểm danh
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

    /**
     * 5. GHI NHẬN ĐIỂM DANH
     * POST /admin/event_register/attendance/record
     */
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