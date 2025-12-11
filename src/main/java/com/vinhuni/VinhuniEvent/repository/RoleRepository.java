package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
