package com.vinhuni.VinhuniEvent.controller;

import com.fasterxml.jackson.core.JsonPointer;
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
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Email không tồn tại!");
            return "auth/login";
        }


        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            model.addAttribute("error", "Mật khẩu sai rồi bạn iu ơi!");
            return "auth/login";
        }

        // lưu user vào session
        session.setAttribute("loggedInUser", user);

        return "redirect:/"; // về trang chủ
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // xóa session
        return "redirect:/login";
    }

}


