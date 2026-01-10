package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_requests")
@Data
public class RoleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Liên kết với bảng users qua user_id

    @Column(name = "requested_role", length = 50)
    private String requestedRole; // Ví dụ: "ORGANIZER"

    @Column(columnDefinition = "TEXT")
    private String reason; // Lý do nâng quyền

    @Column(name = "status", length = 20)
    private String status = "Pending"; // Mặc định là Pending (Chờ duyệt)

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
