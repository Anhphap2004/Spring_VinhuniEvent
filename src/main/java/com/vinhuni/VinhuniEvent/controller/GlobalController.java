package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalController {
    @Autowired
    private UserService userService;
    // Hàm này tự động chạy mỗi khi người dùng tải trang
    @ModelAttribute("loggedInUser")
    public User globalUser(HttpSession session) {
        // 1. Lấy user đang lưu tạm trong session
        User sessionUser = (User) session.getAttribute("loggedInUser");

        // 2. Nếu session có user, ta lấy email để hỏi Database thông tin mới nhất
        if (sessionUser != null) {
            // Đảm bảo UserService của bạn có hàm findByEmail nhé
            User freshUser = userService.findByEmail(sessionUser.getEmail());

            // 3. Cập nhật lại vào session (để lưu quyền mới nhất: role 3)
            if (freshUser != null) {
                session.setAttribute("loggedInUser", freshUser);
                return freshUser; // Trả về cho giao diện (HTML) sử dụng
            }
        }
        return null; // Trả về null nếu chưa đăng nhập
    }
}
