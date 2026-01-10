package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "certificate_code", length = 50, unique = true)
    private String certificateCode;

    @Column(name = "issued_date")
    private LocalDateTime issuedDate;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_valid")
    private Boolean isValid = true;
}

