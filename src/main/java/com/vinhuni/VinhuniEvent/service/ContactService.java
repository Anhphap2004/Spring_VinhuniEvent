package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Contact;

public interface ContactService {
    // Chỉ khai báo tên hàm
    void saveContact(Contact contact, Integer currentUserId);
}
