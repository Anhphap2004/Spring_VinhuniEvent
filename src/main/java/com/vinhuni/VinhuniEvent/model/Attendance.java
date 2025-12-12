package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendances", uniqueConstraints = {
        // THÊM: Unique Constraint để đảm bảo chỉ có 1 bản ghi/Event/User
        @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"event", "user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Attendance {

    // Trong Model Attendance.java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private User user;

    // attendance_time không còn là CreationTimestamp nữa, nó chỉ là thời điểm cập nhật cuối cùng
    @Column(name = "attendance_time", nullable = false)
    private LocalDateTime attendanceTime; // GIỮ LẠI ĐỂ GHI NHẬN THỜI ĐIỂM CẬP NHẬT

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent = true;
}