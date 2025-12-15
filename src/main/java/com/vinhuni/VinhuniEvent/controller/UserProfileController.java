package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Attendance;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.AttendanceService;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import com.vinhuni.VinhuniEvent.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserService userService;
    private final EventRegistrationService registrationService;
    private final AttendanceService attendanceService;

    public UserProfileController(UserService userService,
                                 EventRegistrationService registrationService,
                                 AttendanceService attendanceService) {
        this.userService = userService;
        this.registrationService = registrationService;
        this.attendanceService = attendanceService;
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
}