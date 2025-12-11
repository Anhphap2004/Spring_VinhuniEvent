package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.repository.RoleRepository;
import com.vinhuni.VinhuniEvent.service.RoleService;

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
}
