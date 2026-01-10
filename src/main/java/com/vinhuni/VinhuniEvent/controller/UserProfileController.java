package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Attendance;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.AttendanceService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import com.vinhuni.VinhuniEvent.service.RoleRequestService;
import com.vinhuni.VinhuniEvent.service.UserService;
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
    private final RoleRequestService roleRequestService; // Inject thêm service này

    public UserProfileController(UserService userService,
                                 EventRegistrationService registrationService,
                                 AttendanceService attendanceService, RoleRequestService roleRequestService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
        this.roleRequestService = roleRequestService;
    }

    @GetMapping
    public String myProfile(HttpSession session, Model model) {
        // 1. Kiểm tra session
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // 2. Lấy thông tin User mới nhất từ DB
        User currentUser = userService.findUserById(sessionUser.getUser_id()).orElse(sessionUser);

        // 3. Lấy lịch sử ĐĂNG KÝ
        List<EventRegistration> registrations = registrationService.getRegistrationsByUserId(currentUser.getUser_id());

        // 4. Lấy lịch sử ĐIỂM DANH và chuyển đổi sang Map để dễ tra cứu
        // Key: EventID, Value: Trạng thái (true=Có mặt, false=Vắng)
        List<Attendance> attendances = attendanceService.getAttendancesByUserId(currentUser.getUser_id());
        Map<Long, Boolean> attendanceMap = new HashMap<>();

        for (Attendance att : attendances) {
            // Lưu ý: att.getEvent().getEvent_id() dựa trên model Event của bạn
            attendanceMap.put(att.getEvent().getEvent_id(), att.getIsPresent());
        }

        // 5. Đẩy dữ liệu ra View
        model.addAttribute("user", currentUser);
        model.addAttribute("registrations", registrations);
        model.addAttribute("attendanceMap", attendanceMap); // Map giúp check điểm danh trong vòng lặp

        return "client/profile/index";
    }


    @GetMapping("/request")
    public String showRequestForm(HttpSession session, Model model, RedirectAttributes ra) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            ra.addFlashAttribute("errorMessage", "Vui lòng đăng nhập!");
            return "redirect:/login";
        }

        // 1. Lấy thông tin mới nhất từ DB (Quan trọng để check Role chuẩn)
        User currentUser = userService.findUserById(sessionUser.getUser_id()).orElse(sessionUser);

        // 2. Kiểm tra Role (Giả sử ID 3 là Organizer, bạn check lại DB nhé)
        // Nếu đã là Organizer hoặc Admin
        if (currentUser.getRole().getRole_id() == 3 || currentUser.getRole().getRole_id() == 1) {
            model.addAttribute("requestStatus", "APPROVED"); // Đã là Organizer
            model.addAttribute("pageTitle", "Thông tin quyền hạn");
            return "client/profile/request-upgrade";
        }

        // 3. Kiểm tra xem có đơn nào đang Pending không
        if (roleRequestService.hasPendingRequest(Math.toIntExact(currentUser.getUser_id()))) {
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

            roleRequestService.createRequest(sessionUser, reason);
            ra.addFlashAttribute("successMessage", "Gửi yêu cầu thành công! Vui lòng đợi quản trị viên phê duyệt.");
        } catch (RegistrationException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Đã xảy ra lỗi hệ thống.");
        }


        return "redirect:/profile/request";
    }

}