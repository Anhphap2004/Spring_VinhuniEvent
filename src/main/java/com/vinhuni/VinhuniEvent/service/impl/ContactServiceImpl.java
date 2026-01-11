package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Contact;
import com.vinhuni.VinhuniEvent.repository.ContactRepository;
import com.vinhuni.VinhuniEvent.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service // Bắt buộc phải có để Spring nhận diện đây là nơi xử lý
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public void saveContact(Contact contact, Integer currentUserId) {
        //  Gán ID người dùng nếu họ đã đăng nhập
        if (currentUserId != null) {
            contact.setUserId(currentUserId);
        }

        // 2. Gán ngày giờ hiện tại
        contact.setCreatedAt(new Date());

        // 3. Mặc định tin nhắn mới là chưa đọc (false)
        contact.setRead(false);

        contactRepository.save(contact);
    }
}