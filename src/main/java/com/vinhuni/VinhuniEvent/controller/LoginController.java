package com.vinhuni.VinhuniEvent.controller;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonPointer;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password,
                               HttpSession session, RedirectAttributes ra) {
        try {
            // Đẩy hết việc check cho Service, nếu sai nó tự ném RegistrationException
            User user = userService.authenticate(email, password);

            // Lưu session
            session.setAttribute("loggedInUser", user);

            // Phân quyền điều hướng
            if (user.getRole() != null && user.getRole().getRoleId() == 1) {
                return "redirect:/admin";
            }
            return "redirect:/";

        } catch (RegistrationException e) {
            // Bắt mọi thông điệp lỗi: "Email không tồn tại", "Mật khẩu sai", "Tài khoản bị khóa"
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // xóa session
        return "redirect:/login";
    }

}


