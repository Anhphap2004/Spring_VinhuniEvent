package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void registerUser(User user);
    List<User> findAllUsers();
    Optional<User> findUserById(Long id);
    void saveUser(User user); // Gộp cả Add và Update
    void deleteUser(Long id);
    User findByEmail(String email);

    User authenticate(String email, String password);

    // New: search by roleId + keyword
    List<User> searchUsers(Integer roleId, String keyword);
}