package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.RoleService;
import com.vinhuni.VinhuniEvent.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // Hiển thị danh sách
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/user/list";
    }

    // Xem chi tiết người dùng
    @GetMapping("/view/{id}")
    public String viewUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("pageTitle", "Chi tiết người dùng: " + userOptional.get().getFullName());
            return "admin/user/detail"; // Trả về file view.html trong thư mục admin/user/
        } else {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy người dùng có ID: " + id);
            return "redirect:/admin/users";
        }
    }
    // Form chung cho Thêm mới và Sửa
    @GetMapping({"/new", "/edit/{id}"})
    public String showUserForm(@PathVariable(required = false) Long id, Model model, RedirectAttributes redirectAttributes) {
        User user = new User();
        String pageTitle = "Thêm mới Người dùng";

        if (id != null) {
            Optional<User> userOptional = userService.findUserById(id);
            if (userOptional.isPresent()) {
                user = userOptional.get();
                user.setPasswordHash(""); // Xóa hash để form hiện trống cho an toàn
                pageTitle = "Chỉnh sửa Người dùng (ID: " + id + ")";
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy User ID " + id);
                return "redirect:/admin/users";
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("pageTitle", pageTitle);

        return "admin/user/form";
    }

    // Xử lý lưu (Create/Update)
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute("message", "Lưu người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // Xóa
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi xóa: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}