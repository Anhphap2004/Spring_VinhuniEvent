package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.model.RoleRequest;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.RoleRequestRepository;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.repository.RoleRepository;
import com.vinhuni.VinhuniEvent.service.RoleRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleRequestServiceImpl implements RoleRequestService {

    private final RoleRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleRequestServiceImpl(RoleRequestRepository requestRepository,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void createRequest(User user, String reason) {
        // Logic check nếu đã có yêu cầu đang đợi
        // Giả sử bạn viết thêm method check này trong Repository
        // if (requestRepository.existsByUserAndStatus(user, "Pending")) {
        //    throw new RegistrationException("Yêu cầu của bạn đang được xem xét, đừng gửi thêm nữa!");
        // }

        RoleRequest request = new RoleRequest();
        request.setUser(user);
        request.setRequestedRole("ORGANIZER");
        request.setReason(reason);
        requestRepository.save(request);
    }
    @Override
    public List<RoleRequest> getRequestsByStatus(String status) {
        // Gọi hàm findByStatus đã có sẵn trong JpaRepository (nếu bạn đã khai báo)
        return requestRepository.findByStatus(status);
    }
    @Override
    public List<RoleRequest> getAllPendingRequests() {
        return requestRepository.findByStatus("Pending");
    }

    @Override
    @Transactional // Đảm bảo nếu lỗi thì không đổi gì cả
    public void approveRequest(Integer requestId) {
        // 1. Tìm yêu cầu
        RoleRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RegistrationException("Không tìm thấy yêu cầu!"));

        // 2. Tìm Role "Organizer" (Hãy kiểm tra ID chính xác trong DB của bạn)
        // Giả sử ID 3 là Organizer, ID 1 là Admin, ID 2 là Student
        Role organizerRole = roleRepository.findById(3)
                .orElseThrow(() -> new RegistrationException("Lỗi: Quyền Organizer không tồn tại trong DB!"));

        // 3. Cập nhật quyền cho User
        User user = request.getUser();
        user.setRole(organizerRole);

        // QUAN TRỌNG: Phải lưu User thì role_id trong DB mới đổi
        userRepository.save(user);

        // 4. Cập nhật trạng thái yêu cầu thành Approved
        request.setStatus("Approved");
        requestRepository.save(request);
    }

    @Override
    @Transactional
    public void rejectRequest(Integer requestId) {
        RoleRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RegistrationException("Không tìm thấy yêu cầu này!"));

        request.setStatus("Rejected");
        requestRepository.save(request);
    }
    @Override
    public boolean hasPendingRequest(Integer userId) {
        return requestRepository.existsByUser_UserIdAndStatus(userId, "Pending");
    }
}