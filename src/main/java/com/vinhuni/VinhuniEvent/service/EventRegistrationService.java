package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationService {
    EventRegistration registerForEvent(Long eventId, Long userId);
    void checkIfRegistered(Long eventId, Long userId) throws RegistrationException;
    List<EventRegistration> getRegistrationsByEventId(Long eventId);

    // Cập nhật trạng thái (Dùng cho xác nhận)
    EventRegistration updateRegistrationStatus(Long registrationId, String newStatus);

    // MỚI: Xóa bản ghi đăng ký (Dùng cho hủy)
    void deleteRegistration(Long registrationId) throws RegistrationException;
    Optional<EventRegistration> findRegistration(Long eventId, Long userId);
}