package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    // 1. Tìm bản ghi đăng ký dựa trên ID sự kiện và ID người dùng
    @Query("SELECT r FROM EventRegistration r WHERE r.event.eventId = :eventId AND r.user.userId = :userId")
    Optional<EventRegistration> findByEventAndUserIds(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );

    // 2. Lấy danh sách đăng ký theo Event ID (Để hiển thị danh sách admin)
    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.user WHERE r.event.eventId = :eventId")
    List<EventRegistration> findByEventIdFetchingUser(@Param("eventId") Long eventId);

    // 3. SỬA LỖI TẠI ĐÂY: Lấy lịch sử đăng ký của User
    // Thay vì dùng tên hàm dài dòng dễ gây lỗi, ta dùng câu @Query trực tiếp trỏ vào 'r.user.userId'
    @Query("SELECT r FROM EventRegistration r WHERE r.user.userId = :userId ORDER BY r.registrationDate DESC")
    List<EventRegistration> findByUserId(@Param("userId") Long userId);
}