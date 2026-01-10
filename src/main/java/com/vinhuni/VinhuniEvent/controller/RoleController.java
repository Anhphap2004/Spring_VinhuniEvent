package com.vinhuni.VinhuniEvent.controller;

import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
@Controller
@RequestMapping("/admin/role")

public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String listRoles(Model model) {
        model.addAttribute("roles", roleService.findAllRoles());
        return "admin/role/list";
    }


    @GetMapping({"/add", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Integer id, Model model) {
        Role role = (id != null) ? roleService.getRoleById(id) : new Role();
        model.addAttribute("role", role);
        model.addAttribute("pageTitle", (id != null) ? "Chỉnh sửa Role" : "Thêm Role mới");
        return "admin/role/form";
    }

    @PostMapping("/save")
    public String saveRole(@ModelAttribute("role") Role role) {
        roleService.saveOrUpdate(role);
        return "redirect:/admin/role";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable int id) {
        roleService.deleteRoleById(id);
        return "redirect:/admin/role";
    }
}
