package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.config.RequiredRole;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.service.RoleRequestService;
import com.vinhuni.VinhuniEvent.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@RequiredRole(1)
@Controller
@RequestMapping("/admin/role-requests")
public class RoleRequestController {

    private final RoleRequestService roleRequestService;
    private final UserService userService;

    public RoleRequestController(RoleRequestService roleRequestService, UserService userService) {
        this.roleRequestService = roleRequestService;
        this.userService = userService;
    }

    @GetMapping
    public String listRequestsForAdmin(Model model) {
        // Lấy danh sách theo từng trạng thái
        model.addAttribute("pendingRequests", roleRequestService.getRequestsByStatus("Pending"));
        model.addAttribute("approvedRequests", roleRequestService.getRequestsByStatus("Approved"));
        model.addAttribute("rejectedRequests", roleRequestService.getRequestsByStatus("Rejected"));

        model.addAttribute("pageTitle", "Quản lý yêu cầu nâng quyền");
        return "admin/role-request/list";
    }

    // Xử lý PHÊ DUYỆT (Nút Duyệt)
    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            roleRequestService.approveRequest(id);
            ra.addFlashAttribute("successMessage", "Đã phê duyệt và nâng cấp quyền thành công!");
        } catch (RegistrationException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/role-requests";
    }

    // Xử lý TỪ CHỐI (Nút Từ chối)
    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            roleRequestService.rejectRequest(id);
            ra.addFlashAttribute("successMessage", "Đã từ chối yêu cầu nâng quyền.");
        } catch (RegistrationException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/role-requests";
    }
}