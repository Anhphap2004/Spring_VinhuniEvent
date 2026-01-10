package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.repository.RoleRepository;
import com.vinhuni.VinhuniEvent.service.RoleService;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    public  RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
    public Role getRoleById(int id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
    @Override
    @Transactional
    public Role saveOrUpdate(Role role) {

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
