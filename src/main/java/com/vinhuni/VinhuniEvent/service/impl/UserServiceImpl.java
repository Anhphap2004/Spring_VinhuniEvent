package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
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

        user.setPassword_hash(passwordEncoder.encode(user.getPassword_hash()));
        user.setIs_active(true);
        user.setCreated_date(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id)
                .map(existingUser -> {

                    existingUser.setFull_name(userDetails.getFull_name());
                    existingUser.setEmail(userDetails.getEmail());
                    existingUser.setIs_active(userDetails.getIs_active());
                    existingUser.setStudent_code(userDetails.getStudent_code());
                    existingUser.setFaculty(userDetails.getFaculty());
                    existingUser.setMajor(userDetails.getMajor());
                    existingUser.setBirth_date(userDetails.getBirth_date());
                    existingUser.setPhone_number(userDetails.getPhone_number());
                    existingUser.setImageUrl(userDetails.getImageUrl());


                    existingUser.setRole(userDetails.getRole());


                    if (userDetails.getPassword_hash() != null && !userDetails.getPassword_hash().isEmpty()) {
                        existingUser.setPassword_hash(passwordEncoder.encode(userDetails.getPassword_hash()));
                    }


                    return userRepository.save(existingUser);
                }).orElseThrow(() -> new RuntimeException("User not found with id " + id)); // Xử lý khi không tìm thấy
    }

    @Override
    public User authenticate(String email, String password) {

        User user = userRepository.findByEmail(email);


        if (user == null) {
            throw new RegistrationException("Email không tồn tại!");
        }


        if (!passwordEncoder.matches(password, user.getPassword_hash())) {
            throw new RegistrationException("Mật khẩu không chính xác!");
        }


        if (user.getIs_active() != null && !user.getIs_active()) {
            throw new RegistrationException("Tài khoản đã bị khóa!");
        }


        return user;
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
