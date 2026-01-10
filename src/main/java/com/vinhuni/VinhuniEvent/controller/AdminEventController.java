package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Event;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "keyword", required = false) String keyword) {

        List<Event> list;


        if (keyword != null && !keyword.isEmpty()) {
            list = eventService.searchEvents(keyword);
        } else {
            list = eventService.getAllEvents();
        }


        model.addAttribute("events", list);
        model.addAttribute("keyword", keyword);

        return "admin/event/list";
    }




    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("categories", categoryService.getAllEventCategories());
        return "admin/event/form";
    }


    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") Event event,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) session.getAttribute("loggedInUser");

            if (event.getEvent_id() == null) {
                event.setCreated_date(LocalDateTime.now());
                if (currentUser != null) event.setCreated_by(currentUser);
            } else {
                Optional<Event> existingEvent = eventService.getEventById(event.getEvent_id());
                if(existingEvent.isPresent()){
                    event.setCreated_date(existingEvent.get().getCreated_date());
                    event.setCreated_by(existingEvent.get().getCreated_by());
                    if (imageFile.isEmpty()) {
                        event.setImage(existingEvent.get().getImage());
                    }
                }
            }

            if (event.getSlug() == null || event.getSlug().isEmpty()) {
                event.setSlug(toSlug(event.getTitle()));
            }

            if (!imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                event.setImage("/uploads/" + fileName);
            }

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


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            model.addAttribute("categories", categoryService.getAllEventCategories());
            return "admin/event/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sự kiện ID: " + id);
            return "redirect:/admin/events";
        }
    }


    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        Optional<Event> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            return "admin/event/detail";
        }
        return "redirect:/admin/events";
    }


    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sự kiện thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sự kiện này.");
        }
        return "redirect:/admin/events";
    }

    private String toSlug(String input) {
        if (input == null) return "";
        return input.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", "-");
    }
}