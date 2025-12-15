package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventCategory;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import com.vinhuni.VinhuniEvent.service.EventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;
    private final EventCategoryService categoryService;
    private final String UPLOAD_DIR = "uploads/";
    public AdminEventController(EventService eventService, EventCategoryService categoryService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
    }

    // 1. HIỂN THỊ DANH SÁCH (LIST)
    @GetMapping
    public String index(Model model) {
        List<Event> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "admin/event/list";
    }

    // 2. HIỂN THỊ FORM THÊM MỚI (CREATE GET)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryService.getAllEventCategories()); // Load danh mục để chọn
        return "admin/event/form";
    }

    // 3. XỬ LÝ LƯU SỰ KIỆN (CREATE/UPDATE POST)
    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event,
                            @RequestParam("imageFile") MultipartFile imageFile, // Nhận file từ form
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("loggedInUser");

            // 1. Xử lý Logic Create/Update cơ bản (Ngày tạo, người tạo)
            if (event.getEvent_id() == null) {
                event.setCreated_date(LocalDateTime.now());
                if (currentUser != null) event.setCreated_by(currentUser);
            } else {
                // Nếu là Edit: Lấy thông tin cũ để giữ lại những thứ không có trong form
                Optional<Event> existingEvent = eventService.getEventById(event.getEvent_id());
                if(existingEvent.isPresent()){
                    event.setCreated_date(existingEvent.get().getCreated_date());
                    event.setCreated_by(existingEvent.get().getCreated_by());

                    // Nếu người dùng KHÔNG upload ảnh mới, giữ lại ảnh cũ
                    if (imageFile.isEmpty()) {
                        event.setImage(existingEvent.get().getImage());
                    }
                }
            }

            // Xử lý Slug
            if (event.getSlug() == null || event.getSlug().isEmpty()) {
                event.setSlug(toSlug(event.getTitle()));
            }

            // 2. XỬ LÝ UPLOAD ẢNH (Quan trọng)
            if (!imageFile.isEmpty()) {
                // Tạo tên file duy nhất (UUID) để tránh trùng lặp
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

                // Tạo thư mục nếu chưa tồn tại
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Lưu file vào thư mục uploads
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Lưu đường dẫn vào Database (để hiển thị trên web)
                event.setImage("/uploads/" + fileName);
            }

            // 3. Lưu vào DB
            eventService.saveEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu sự kiện thành công!");

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi upload ảnh: " + e.getMessage());
            return "redirect:/admin/events/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/admin/events/create";
        }

        return "redirect:/admin/events";
    }

    // 4. HIỂN THỊ FORM CHỈNH SỬA (EDIT GET)
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            model.addAttribute("categories", categoryService.getAllEventCategories());
            return "admin/event/form"; // Tái sử dụng form
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sự kiện ID: " + id);
            return "redirect:/admin/events";
        }
    }

    // 5. XEM CHI TIẾT (DETAIL)
    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            return "admin/event/detail";
        }
        return "redirect:/admin/events";
    }

    // 6. XÓA (DELETE)
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sự kiện thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sự kiện này (có thể do ràng buộc dữ liệu).");
        }
        return "redirect:/admin/events";
    }

    // Hàm tiện ích tạo Slug đơn giản (nên chuyển vào Service hoặc Util class)
    private String toSlug(String input) {
        if (input == null) return "";
        return input.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");
    }
}