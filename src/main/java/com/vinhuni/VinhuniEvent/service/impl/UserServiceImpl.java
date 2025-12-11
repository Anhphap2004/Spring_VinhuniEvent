package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
