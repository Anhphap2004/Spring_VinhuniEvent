package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.model.Role; // Giả định bạn có Role model và RoleService
import com.vinhuni.VinhuniEvent.service.UserService;
import com.vinhuni.VinhuniEvent.service.RoleService; // Cần thiết để lấy danh sách Role
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users") // Base URL cho quản lý người dùng
public class UserController {

    private final UserService userService;
    private final RoleService roleService; // Cần thiết để quản lý Role trong form


    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 1. Xem danh sách tất cả người dùng
     * GET /admin/users
     * Trả về view: users/list
     */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        // Trả về tên file HTML (ví dụ: src/main/resources/templates/users/list.html)
        return "/admin/user/list";
    }


    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User()); // Đối tượng User rỗng
        model.addAttribute("pageTitle", "Thêm mới Người dùng");

        // Cần truyền danh sách Roles để hiển thị trong <select>
        List<Role> roles = roleService.findAllRoles(); // Giả định RoleService có hàm findAllRoles
        model.addAttribute("roles", roles);

        return "admin/user/form";
    }

    /**
     * 3. Xử lý LƯU (Thêm mới HOẶC Cập nhật)
     * POST /admin/users/save
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra xem là Thêm mới hay Cập nhật
        if (user.getUser_id() == null) {
            // Đây là Thêm mới (registerUser sẽ xử lý mã hóa mật khẩu)
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("message", "Người dùng '" + user.getFull_name() + "' đã được thêm mới thành công!");
        } else {
            // Đây là Cập nhật (updateUser sẽ xử lý bảo toàn mật khẩu và các trường khác)
            // Lưu ý: Cần truyền userDetails (là user từ form) vào
            userService.updateUser(user.getUser_id(), user);
            redirectAttributes.addFlashAttribute("message", "Thông tin người dùng '" + user.getFull_name() + "' đã được cập nhật thành công!");
        }

        return "redirect:/admin/users";
    }

    /**
     * 4. Hiển thị form Chỉnh sửa
     * GET /admin/users/edit/{id}
     * Trả về view: users/form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("pageTitle", "Chỉnh sửa Người dùng (ID: " + id + ")");

            // Cần truyền danh sách Roles để form hiển thị lựa chọn
            List<Role> roles = roleService.findAllRoles();
            model.addAttribute("roles", roles);

            return "admin/user/form";
        } else {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Không tìm thấy Người dùng có ID " + id);
            return "redirect:/admin/users";
        }
    }

    /**
     * 5. Xử lý Xóa
     * GET /admin/users/delete/{id}
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            try {
                String userName = userOptional.get().getFull_name();
                userService.deleteUser(id);
                redirectAttributes.addFlashAttribute("message", "Người dùng '" + userName + "' (ID: " + id + ") đã được xóa thành công!");
            } catch (Exception e) {
                // Xử lý lỗi nếu có vấn đề khóa ngoại, v.v.
                redirectAttributes.addFlashAttribute("message", "Lỗi xóa Người dùng ID " + id + ": " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Lỗi: Không tìm thấy Người dùng có ID " + id + " để xóa.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/view/{id}")
    public String viewUserDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findUserById(id);

        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("pageTitle", "Chi tiết Người dùng (ID: " + id + ")");

            // Trả về tên file HTML mới
            return "admin/user/detail";
        } else {
            // Trường hợp không tìm thấy, chuyển hướng về danh sách với thông báo lỗi
            redirectAttributes.addFlashAttribute("message", "Lỗi: Không tìm thấy Người dùng có ID " + id + " để xem chi tiết.");
            return "redirect:/admin/users";
        }
    }
}