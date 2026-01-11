package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Contact;
import com.vinhuni.VinhuniEvent.repository.ContactRepository;
import com.vinhuni.VinhuniEvent.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

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
    // 1. Lấy toàn bộ danh sách liên hệ (Sắp xếp mới nhất lên đầu)
    public List<Contact> getAllContacts() {
        // Sort.by cần import org.springframework.data.domain.Sort;
        return contactRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // 2. Xóa liên hệ theo ID
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }
}