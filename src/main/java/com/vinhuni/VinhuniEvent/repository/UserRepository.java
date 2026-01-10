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

    // Filter theo role_id và keyword
    @Query(value = """
        SELECT u.* FROM users u 
        LEFT JOIN roles r ON r.role_id = u.role_id
        WHERE (:roleId IS NULL OR u.role_id = :roleId)
          AND (:keyword IS NULL OR :keyword = ''
               OR LOWER(CAST(u.full_name AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%'))
               OR LOWER(CAST(u.email AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%'))
               OR LOWER(CAST(u.student_code AS TEXT)) LIKE LOWER(CONCAT('%', CAST(:keyword AS TEXT), '%')))
        """, nativeQuery = true)
    List<User> searchUsers(@Param("roleId") Integer roleId, @Param("keyword") String keyword);
}