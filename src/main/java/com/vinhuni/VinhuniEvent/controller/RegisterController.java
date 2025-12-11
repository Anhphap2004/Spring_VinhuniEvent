package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.RoleService;
import com.vinhuni.VinhuniEvent.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RegisterController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }
    @PostMapping("/register")
    public String processRegister(
            @ModelAttribute("user") User user,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model
    ) {
        String rawPassword = user.getPassword_hash();
        if (!rawPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            return "auth/register";
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("error", "Email này đã được sử dụng!");
            return "auth/register";
        }

        int DEFAULT_ROLE_ID = 2;
        Role defaultRole = roleService.getRoleById(DEFAULT_ROLE_ID);

        if (defaultRole == null) {
            model.addAttribute("error", "Role mặc định không tồn tại, vui lòng kiểm tra CSDL.");
            return "auth/register";
        }
        user.setRole(defaultRole);

        userService.registerUser(user);

        model.addAttribute("success", "Đăng ký thành công! Mời bạn đăng nhập.");
        return "auth/login";
    }
}