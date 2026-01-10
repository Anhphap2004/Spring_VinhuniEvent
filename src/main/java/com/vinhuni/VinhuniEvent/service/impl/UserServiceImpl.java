package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Role;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.service.RoleService;
import com.vinhuni.VinhuniEvent.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public void registerUser(User user) {
        // mã hóa pass
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setIsActive(true);
        user.setCreatedDate(LocalDateTime.now());
        userRepository.save(user);
    }
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        // 1. Trường hợp Thêm mới (ID chưa có)
        if (user.getUserId() == null) {
            user.setCreatedDate(LocalDateTime.now());
            user.setIsActive(true);
            // Mã hóa mật khẩu
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

            // Xử lý Role: nếu có roleId, fetch Role entity
            if (user.getRole() != null && user.getRole().getRoleId() != null) {
                Role role = roleService.getRoleById(user.getRole().getRoleId());
                user.setRole(role);
            }

            userRepository.save(user);
        }
        // 2. Trường hợp Cập nhật (Đã có ID)
        else {
            User existingUser = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

            // Cập nhật các trường thông tin chung
            existingUser.setFullName(user.getFullName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setStudentCode(user.getStudentCode());
            existingUser.setFaculty(user.getFaculty());
            existingUser.setMajor(user.getMajor());
            existingUser.setIsActive(user.getIsActive());

            // Xử lý Role: fetch Role entity từ roleId
            if (user.getRole() != null && user.getRole().getRoleId() != null) {
                Role role = roleService.getRoleById(user.getRole().getRoleId());
                existingUser.setRole(role);
            }

            // LOGIC MẬT KHẨU:
            // Nếu form gửi lên mật khẩu mới (khác rỗng) -> Mã hóa và lưu
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            // Nếu để trống -> Giữ nguyên mật khẩu cũ (không làm gì cả)

            userRepository.save(existingUser);
        }
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RegistrationException("Email không tồn tại!");
        }
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RegistrationException("Mật khẩu không chính xác!");
        }
        if (user.getIsActive() != null && !user.getIsActive()) {
            throw new RegistrationException("Tài khoản đã bị khóa!");
        }
        return user;
    }

    @Override
    public List<User> searchUsers(Integer roleId, String keyword) {
        String keywordParam = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return userRepository.searchUsers(roleId, keywordParam);
    }
}