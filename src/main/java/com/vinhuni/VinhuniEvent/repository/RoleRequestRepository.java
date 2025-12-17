package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.RoleRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRequestRepository extends JpaRepository<RoleRequest, Integer> {
    // Tìm các request theo trạng thái (cho Admin)
    List<RoleRequest> findByStatus(String status);

    // THÊM DÒNG NÀY: Kiểm tra xem User này có đơn nào đang ở trạng thái Pending không
    boolean existsByUser_UserIdAndStatus(Integer userId, String status);
}