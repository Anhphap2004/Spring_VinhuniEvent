package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString // Sử dụng ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // Map với cột user_id trong SQL
    private Long userId;      // Tên biến chuẩn Java

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 200, nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ToString.Exclude
    private Role role;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "student_code", length = 20)
    private String studentCode;

    @Column(length = 100)
    private String faculty;

    @Column(length = 100)
    private String major;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<EventRegistration> eventRegistrations = new HashSet<>();
}