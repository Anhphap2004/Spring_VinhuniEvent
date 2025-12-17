package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Role;
import java.util.List;

public interface RoleService {
    Role getRoleById(int id);
    List<Role> findAllRoles();
    Role saveOrUpdate(Role role); // Hợp nhất thêm và sửa
    void deleteRoleById(int id);
}