package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.RoleRequest;
import com.vinhuni.VinhuniEvent.model.User;
import java.util.List;

public interface RoleRequestService {
    // Các hàm đã có
    void createRequest(User user, String reason);
    List<RoleRequest> getAllPendingRequests();
    void approveRequest(Integer requestId);
    void rejectRequest(Integer requestId);
    boolean hasPendingRequest(Integer userId);

    // THÊM HÀM NÀY ĐỂ LỌC THEO TRẠNG THÁI
    List<RoleRequest> getRequestsByStatus(String status);
}