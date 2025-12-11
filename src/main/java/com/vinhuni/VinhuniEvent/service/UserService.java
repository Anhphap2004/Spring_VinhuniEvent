package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.User;

public interface UserService {
    void registerUser(User user);
    User findByEmail(String email);
}
