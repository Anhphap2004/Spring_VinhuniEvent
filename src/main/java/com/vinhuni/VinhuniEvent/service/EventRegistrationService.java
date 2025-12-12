package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import java.util.List;

public interface EventRegistrationService {
    EventRegistration registerForEvent(Long eventId, Long userId);
    void checkIfRegistered(Long eventId, Long userId) throws RegistrationException;

    // Phương thức mới: Lấy danh sách đăng ký chi tiết
    List<EventRegistration> getRegistrationsByEventId(Long eventId);

    // Phương thức mới: Cập nhật trạng thái
    EventRegistration updateRegistrationStatus(Long registrationId, String newStatus);
}