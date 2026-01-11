package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/contacts") // Đường dẫn chung bắt đầu bằng /admin/contacts
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    // 1. Hiển thị danh sách
    @GetMapping
    public String listContacts(Model model) {
        model.addAttribute("contacts", contactService.getAllContacts());
        return "admin/contact/contact-list"; // Trả về file HTML trong thư mục templates/admin
    }

    // 2. Xóa liên hệ
    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") Long id) {
        contactService.deleteContact(id);
        return "redirect:/admin/contacts"; // Xóa xong load lại trang danh sách
    }
}