package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Contact;

import java.util.List;

public interface ContactService {
    // Chỉ khai báo tên hàm
    void saveContact(Contact contact, Integer currentUserId);
    // 1. Hàm lấy danh sách
    List<Contact> getAllContacts();

    // 2. Hàm xóa
    void deleteContact(Long id);
}
