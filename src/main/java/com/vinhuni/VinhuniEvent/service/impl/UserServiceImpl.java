package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void registerUser(User user) {
        // mã hóa pass
        user.setPassword_hash(passwordEncoder.encode(user.getPassword_hash()));
        user.setIs_active(true);
        user.setCreated_date(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Bổ sung: Lấy tất cả người dùng (Xem danh sách)
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    // Bổ sung: Lấy người dùng theo ID (Xem chi tiết)
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    // Bổ sung: Cập nhật người dùng
    @Override
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Cập nhật thông tin cơ bản
                    existingUser.setFull_name(userDetails.getFull_name());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setIs_active(userDetails.getIs_active());
                    existingUser.setStudent_code(userDetails.getStudent_code());
                    existingUser.setFaculty(userDetails.getFaculty());
                    existingUser.setMajor(userDetails.getMajor());
                    existingUser.setBirth_date(userDetails.getBirth_date());
                    existingUser.setPhone_number(userDetails.getPhone_number());
                    existingUser.setImageUrl(userDetails.getImageUrl());

                    // Cập nhật Role (nếu cần)
                    existingUser.setRole(userDetails.getRole());

                    // Nếu password_hash mới khác rỗng, tiến hành mã hóa và cập nhật
                    // Lưu ý: Trong thực tế, bạn nên có một API riêng cho việc đổi mật khẩu.
                    if (userDetails.getPassword_hash() != null && !userDetails.getPassword_hash().isEmpty()) {
                        existingUser.setPassword_hash(passwordEncoder.encode(userDetails.getPassword_hash()));
                    }

                    // Lưu và trả về người dùng đã cập nhật
                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with id " + id)); // Xử lý khi không tìm thấy
    }



    @Override
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }
}
