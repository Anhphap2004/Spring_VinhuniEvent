package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.repository.RoleRepository;
import com.vinhuni.VinhuniEvent.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role getRoleById(int id) {
        // Sử dụng .orElseThrow để xử lý lỗi nếu không tìm thấy ID
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleNameIgnoreCase(roleName);
    }

    @Override
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu
    public Role saveOrUpdate(Role role) {
        // Spring Data JPA tự động hiểu:
        // Nếu role.getRoleId() == null -> INSERT
        // Nếu role.getRoleId() != null -> UPDATE
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRoleById(int id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        }
    }
}