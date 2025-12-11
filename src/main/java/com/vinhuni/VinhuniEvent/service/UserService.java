package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    // Phương thức đã có: Đăng ký (Thêm mới)
    void registerUser(User user);

    // Phương thức đã có: Tìm theo Email
    User findByEmail(String email);

    // Bổ sung: Lấy tất cả người dùng
    List<User> findAllUsers();

    // Bổ sung: Lấy người dùng theo ID
    Optional<User> findUserById(Long id);

    // Bổ sung: Cập nhật người dùng (dùng chung với registerUser nhưng nên đặt tên rõ ràng hơn cho thao tác Admin)
    User updateUser(Long id, User userDetails);

    // Bổ sung: Xóa người dùng theo ID
    void deleteUser(Long id);
}