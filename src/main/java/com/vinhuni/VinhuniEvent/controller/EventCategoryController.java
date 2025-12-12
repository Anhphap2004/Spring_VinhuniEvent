package com.vinhuni.VinhuniEvent.controller;
import com.vinhuni.VinhuniEvent.model.EventCategory;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/event_categories")
public class EventCategoryController {
    private EventCategoryService eventCategoryService;
    public EventCategoryController(EventCategoryService eventCategoryService) {
        this.eventCategoryService = eventCategoryService;
    }
    @GetMapping
    public String showEventCategories(Model model) {
        List<EventCategory> eventCategories = eventCategoryService.getAllEventCategories();
        model.addAttribute("eventCategories", eventCategories);
        return "admin/event_category/list";
    }
    @GetMapping("/new")
    public String showAddForm(Model model) {
        // Cần một đối tượng EventCategory rỗng để form Thymeleaf ánh xạ (th:object)
        model.addAttribute("category", new EventCategory());
        model.addAttribute("pageTitle", "Thêm mới Danh mục Sự kiện");
        return "admin/event_category/form";
    }
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") EventCategory category,
                               RedirectAttributes redirectAttributes) {
        eventCategoryService.saveEventCategory(category);

        redirectAttributes.addFlashAttribute("message", "Danh mục đã được lưu thành công!");

        return "redirect:/event_categories";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<EventCategory> category = eventCategoryService.getEventCategoryById(id);

        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            model.addAttribute("pageTitle", "Chỉnh sửa Danh mục Sự kiện (ID: " + id + ")");
            return "admin/event_category/form";
        } else {
            // Trường hợp không tìm thấy ID, chuyển hướng và thông báo lỗi
            redirectAttributes.addFlashAttribute("message", "Lỗi: Không tìm thấy Danh mục có ID " + id);
            return "redirect:/event_categories";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            eventCategoryService.deleteEventCategory(id);
            redirectAttributes.addFlashAttribute("message", "Danh mục ID " + id + " đã được xóa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi xóa Danh mục ID " + id + ": " + e.getMessage());
        }
        return "redirect:/event_categories";
    }
}
