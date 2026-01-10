package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


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
            RedirectAttributes ra) {


        User user = userService.findByEmail(email);


        if (user == null) {
            ra.addFlashAttribute("errorMessage", "Email không tồn tại trong hệ thống!");
            return "redirect:/login";
        }


        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            ra.addFlashAttribute("errorMessage", "Mật khẩu không đúng, vui lòng thử lại!");
            return "redirect:/login";
        }


        session.setAttribute("loggedInUser", user);


        if (user.getRole() != null && user.getRole().getRole_id() == 1) {
            return "redirect:/admin";
        }


        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}