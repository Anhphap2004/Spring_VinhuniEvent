package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Certificate;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.CertificateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    /**
     * Cấp giấy chứng nhận cho user sau khi điểm danh thành công
     */
    @Transactional
    public Certificate issueCertificate(User user, Event event, String issuedBy) {
        // Kiểm tra đã có giấy chứng nhận chưa
        if (certificateRepository.existsByUserUserIdAndEventEventId(user.getUserId(), event.getEventId())) {
            throw new RuntimeException("Người dùng đã được cấp giấy chứng nhận cho sự kiện này!");
        }

        // Tạo mã giấy chứng nhận unique
        String certificateCode = generateCertificateCode(event.getEventId(), user.getUserId());

        Certificate certificate = Certificate.builder()
                .user(user)
                .event(event)
                .certificateCode(certificateCode)
                .issuedDate(LocalDateTime.now())
                .issuedBy(issuedBy)
                .description("Giấy chứng nhận tham gia sự kiện: " + event.getTitle())
                .isValid(true)
                .build();

        return certificateRepository.save(certificate);
    }

    /**
     * Lấy danh sách giấy chứng nhận của user
     */
    public List<Certificate> getCertificatesByUserId(Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    /**
     * Lấy giấy chứng nhận theo event
     */
    public List<Certificate> getCertificatesByEventId(Long eventId) {
        return certificateRepository.findByEventId(eventId);
    }

    /**
     * Kiểm tra user đã có giấy chứng nhận cho event chưa
     */
    public boolean hasCertificate(Long userId, Long eventId) {
        return certificateRepository.existsByUserUserIdAndEventEventId(userId, eventId);
    }

    /**
     * Lấy giấy chứng nhận của user cho event cụ thể
     */
    public Optional<Certificate> getCertificate(Long userId, Long eventId) {
        return certificateRepository.findByUserIdAndEventId(userId, eventId);
    }

    /**
     * Tìm giấy chứng nhận theo mã
     */
    public Optional<Certificate> findByCertificateCode(String code) {
        return certificateRepository.findByCertificateCode(code);
    }

    /**
     * Tạo mã giấy chứng nhận unique
     * Format: CERT-{eventId}-{userId}-{random}
     */
    private String generateCertificateCode(Long eventId, Long userId) {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("CERT-%d-%d-%s", eventId, userId, uuid);
    }
}

