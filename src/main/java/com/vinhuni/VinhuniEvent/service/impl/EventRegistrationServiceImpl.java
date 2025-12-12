package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.EventRegistrationRepository;
import com.vinhuni.VinhuniEvent.repository.EventRepository;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.service.EventRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventRegistrationServiceImpl implements EventRegistrationService {

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public EventRegistration registerForEvent(Long eventId, Long userId) {
        // ... (Giữ nguyên logic kiểm tra tồn tại Event và User) ...
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RegistrationException("Sự kiện không tồn tại."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RegistrationException("Người dùng không tồn tại."));

        if (registrationRepository.findByEventAndUserIds(eventId, userId).isPresent()) {
            throw new RegistrationException("Người dùng đã đăng ký sự kiện này rồi.");
        }

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        // YÊU CẦU 2: Hiển thị status là 'Đã đăng ký' khi mới tạo
        registration.setStatus("Đã đăng ký");

        return registrationRepository.save(registration);
    }

    // ... (Giữ nguyên các hàm checkIfRegistered, getRegistrationsByEventId) ...
    @Override
    @Transactional(readOnly = true)
    public void checkIfRegistered(Long eventId, Long userId) throws RegistrationException {
        Optional<EventRegistration> registration =
                registrationRepository.findByEventAndUserIds(eventId, userId);
        if (registration.isEmpty()) {
            throw new RegistrationException("Người dùng chưa đăng ký sự kiện này.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventRegistration> getRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEventIdFetchingUser(eventId);
    }

    @Override
    @Transactional
    public EventRegistration updateRegistrationStatus(Long registrationId, String newStatus) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new RegistrationException("Bản ghi đăng ký không tồn tại."));

        registration.setStatus(newStatus);
        return registrationRepository.save(registration);
    }

    // YÊU CẦU 1: Xóa bản ghi khỏi bảng đăng ký
    @Override
    @Transactional
    public void deleteRegistration(Long registrationId) throws RegistrationException {
        if (!registrationRepository.existsById(registrationId)) {
            throw new RegistrationException("Bản ghi đăng ký không tồn tại để xóa.");
        }
        registrationRepository.deleteById(registrationId);
    }
    @Override
    public Optional<EventRegistration> findRegistration(Long eventId, Long userId) {
        return registrationRepository.findByEventAndUserIds(eventId, userId);
    }
}