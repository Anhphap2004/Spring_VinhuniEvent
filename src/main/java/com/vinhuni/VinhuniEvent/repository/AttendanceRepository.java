package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * JPQL: Tìm bản ghi điểm danh MỚI NHẤT (latest) cho một người dùng tại sự kiện.
     * Dùng để xác định trạng thái cuối cùng (Có mặt/Vắng mặt).
     */
    @Query("SELECT a FROM Attendance a WHERE a.event.event_id = :eventId AND a.user.user_id = :userId ORDER BY a.attendanceTime DESC LIMIT 1")
    Optional<Attendance> findTopByEventIdAndUserIdOrderByAttendanceTime(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );

    /**
     * JPQL: Lấy tất cả các bản ghi điểm danh của một sự kiện (cho Admin).
     * JOIN FETCH User để tránh lỗi Lazy Loading khi hiển thị chi tiết người dùng.
     */
    @Query("SELECT a FROM Attendance a JOIN FETCH a.user WHERE a.event.event_id = :eventId ORDER BY a.attendanceTime DESC")
    List<Attendance> findByEventIdFetchingUser(@Param("eventId") Long eventId);
}