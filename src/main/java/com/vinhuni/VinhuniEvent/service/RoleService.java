package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role getRoleById(int id);
    Optional<Role> findByRoleName(String roleName);
    List<Role> findAllRoles();
    Role saveOrUpdate(Role role); // Hợp nhất thêm và sửa
    void deleteRoleById(int id);
}