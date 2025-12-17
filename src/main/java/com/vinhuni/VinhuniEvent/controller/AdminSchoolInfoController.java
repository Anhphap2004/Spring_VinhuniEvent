package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.SchoolInfo;
import com.vinhuni.VinhuniEvent.service.SchoolInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/school-info")
public class AdminSchoolInfoController {

    private final SchoolInfoService schoolInfoService;

    public AdminSchoolInfoController(SchoolInfoService schoolInfoService) {
        this.schoolInfoService = schoolInfoService;
    }
    @GetMapping()
    public String showAdminInfo(Model model) {
        // Lấy dữ liệu hiện tại để điền vào Form
        model.addAttribute("school", schoolInfoService.getSchoolInfo());
        model.addAttribute("pageTitle", "Cấu hình thông tin trường học");

        return "admin/school-info/index";
    }
    // Xem form chỉnh sửa
    @GetMapping("/edit")
    public String editForm(Model model) {
        model.addAttribute("school", schoolInfoService.getSchoolInfo());
        model.addAttribute("pageTitle", "Chỉnh sửa thông tin trường");
        return "admin/school-info/edit";
    }

    // Lưu thông tin
    @PostMapping("/save")
    public String save(@ModelAttribute SchoolInfo schoolInfo, RedirectAttributes ra) {
        try {
            schoolInfoService.saveSchoolInfo(schoolInfo);
            ra.addFlashAttribute("successMessage", "Cập nhật thông tin trường thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/school-info/edit";
    }
}