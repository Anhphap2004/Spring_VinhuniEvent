package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Contact;
import com.vinhuni.VinhuniEvent.service.ContactService; // Import Interface
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService; // Gọi Interface, không gọi Impl trực tiếp

    // 1. Hiển thị form liên hệ
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "main/contact/contact";
    }

    // 2. Xử lý lưu form khi bấm Gửi
    @PostMapping("/contact/save")
    public String saveContact(@ModelAttribute("contact") Contact contact,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        Integer currentUserId = null;

        // Kiểm tra xem user có đăng nhập không
        if (principal != null) {
            // TODO: Sau này em sẽ lấy ID thật từ UserService dựa vào principal.getName()
            // Ví dụ tạm thời gán cứng là 1 để test
            currentUserId = 1;
        }

        // Gọi hàm save trong Interface
        contactService.saveContact(contact, currentUserId);

        redirectAttributes.addFlashAttribute("message", "Cảm ơn bạn! Chúng tôi đã nhận được phản hồi.");
        return "redirect:/contact";
    }
}