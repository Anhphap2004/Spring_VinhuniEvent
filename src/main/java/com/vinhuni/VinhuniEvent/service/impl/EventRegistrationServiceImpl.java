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
        // 1. Kiểm tra sự tồn tại của Sự kiện
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RegistrationException("Sự kiện không tồn tại."));

        // 2. Kiểm tra sự tồn tại của Người dùng
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RegistrationException("Người dùng không tồn tại."));

        // 3. Kiểm tra đã đăng ký chưa
        if (registrationRepository.findByEventAndUserIds(eventId, userId).isPresent()) {
            throw new RegistrationException("Người dùng đã đăng ký sự kiện này rồi.");
        }

        // 4. Tạo đối tượng đăng ký mới
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registration.setStatus("Đã đăng ký");

        // 5. Lưu vào DB
        return registrationRepository.save(registration);
    }

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
        // Dùng phương thức JPQL JOIN FETCH để lấy danh sách an toàn
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
}