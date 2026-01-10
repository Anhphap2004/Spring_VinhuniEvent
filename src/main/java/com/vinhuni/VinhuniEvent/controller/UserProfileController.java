package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Attendance;
import com.vinhuni.VinhuniEvent.model.Certificate;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;
    private final EventRegistrationService registrationService;
    private final AttendanceService attendanceService;
    private final RoleRequestService roleRequestService;
    private final QRCodeService qrCodeService;
    private final CertificateService certificateService;

    public UserProfileController(UserService userService,
                                 EventRegistrationService registrationService,
                                 AttendanceService attendanceService,
                                 RoleRequestService roleRequestService,
                                 QRCodeService qrCodeService,
                                 CertificateService certificateService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
        this.roleRequestService = roleRequestService;
        this.qrCodeService = qrCodeService;
        this.certificateService = certificateService;
    }

    @GetMapping
    public String myProfile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) return "redirect:/login";

        User currentUser = userService.findUserById(sessionUser.getUserId()).orElse(sessionUser);
        List<EventRegistration> registrations = registrationService.getRegistrationsByUserId(currentUser.getUserId());

        List<Attendance> attendances = attendanceService.getAttendancesByUserId(currentUser.getUserId());
        Map<Long, Boolean> attendanceMap = new HashMap<>();
        for (Attendance att : attendances) {
            attendanceMap.put(att.getEvent().getEventId(), att.getIsPresent());
        }

        // Lấy mã QR cá nhân
        String qrContent = qrCodeService.createUserQRContent(currentUser.getUserId(), currentUser.getStudentCode());
        String qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrContent, 250, 250);

        // Lấy danh sách giấy chứng nhận
        List<Certificate> certificates = certificateService.getCertificatesByUserId(currentUser.getUserId());

        // Tạo map certificate theo eventId để hiển thị
        Map<Long, Certificate> certificateMap = new HashMap<>();
        for (Certificate cert : certificates) {
            certificateMap.put(cert.getEvent().getEventId(), cert);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("registrations", registrations);
        model.addAttribute("attendanceMap", attendanceMap);
        model.addAttribute("qrCode", "data:image/png;base64," + qrCodeBase64);
        model.addAttribute("certificates", certificates);
        model.addAttribute("certificateMap", certificateMap);

        return "client/profile/index";
    }
// Trong UserProfileController.java

    @GetMapping("/request")
    public String showRequestForm(HttpSession session, Model model, RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            ra.addFlashAttribute("errorMessage", "Vui lòng đăng nhập!");
            return "redirect:/login";
        }

        // 1. Lấy thông tin mới nhất từ DB (Quan trọng để check Role chuẩn)
        User currentUser = userService.findUserById(sessionUser.getUserId()).orElse(sessionUser);

        // 2. Kiểm tra Role (Giả sử ID 3 là Organizer, bạn check lại DB nhé)
        // Nếu đã là Organizer hoặc Admin
        if (currentUser.getRole().getRoleId() == 3 || currentUser.getRole().getRoleId() == 1) {
            model.addAttribute("requestStatus", "APPROVED"); // Đã là Organizer
            model.addAttribute("pageTitle", "Thông tin quyền hạn");
            return "client/profile/request-upgrade";
        }

        // 3. Kiểm tra xem có đơn nào đang Pending không
        if (roleRequestService.hasPendingRequest(Math.toIntExact(currentUser.getUserId()))) {
            model.addAttribute("requestStatus", "PENDING"); // Đang chờ duyệt
            model.addAttribute("pageTitle", "Trạng thái yêu cầu");
            return "client/profile/request-upgrade";
        }

        // 4. Chưa có gì cả -> Hiện Form
        model.addAttribute("requestStatus", "NEW");
        model.addAttribute("pageTitle", "Gửi yêu cầu nâng quyền");
        return "client/profile/request-upgrade";
    }
    @PostMapping("/submit")
    public String submitRequest(@RequestParam String reason, HttpSession session, RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) return "redirect:/login";

        try {
            // Thực hiện lưu yêu cầu vào DB
            roleRequestService.createRequest(sessionUser, reason);
            ra.addFlashAttribute("successMessage", "Gửi yêu cầu thành công! Vui lòng đợi quản trị viên phê duyệt.");
        } catch (RegistrationException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống.");
        }

        // Sau khi gửi xong, quay lại trang request để thấy trạng thái PENDING
        return "redirect:/profile/request";
    }
}
