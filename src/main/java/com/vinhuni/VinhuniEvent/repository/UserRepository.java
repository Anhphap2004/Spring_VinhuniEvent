package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    // Tìm users theo roleId
    @Query("SELECT u FROM User u WHERE u.role.roleId = :roleId")
    List<User> findByRoleId(@Param("roleId") Integer roleId);

    // Filter theo role_id và keyword - Sử dụng JPQL đơn giản
    @Query("SELECT u FROM User u LEFT JOIN u.role r WHERE " +
           "(:roleId IS NULL OR u.role.roleId = :roleId) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("roleId") Integer roleId, @Param("keyword") String keyword);
}