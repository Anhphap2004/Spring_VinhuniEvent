package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.RoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Integer> {

    // Tìm các request theo trạng thái
    List<RoleRequest> findByStatus(String status);

    // --- SỬA Ở ĐÂY: DÙNG @QUERY ---
    // Logic: Đếm xem có bản ghi nào của user.id này và trạng thái này không.
    // Nếu count > 0 trả về true, ngược lại false.
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM RoleRequest r WHERE r.user.user_id = :userId AND r.status = :status")
    boolean existsByUser_UserIdAndStatus(@Param("userId") Integer userId,
                                         @Param("status") String status);

}