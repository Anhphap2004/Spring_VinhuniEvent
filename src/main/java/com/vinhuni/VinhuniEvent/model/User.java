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
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Cấu hình EqualsAndHashCode an toàn
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // BAO GỒM KHÓA CHÍNH
    private Long user_id;

    @Column(name = "full_name", length = 100, nullable = false)
    private String full_name;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", length = 200, nullable = false)
    private String password_hash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Role role;

    @Column(name = "is_active")
    private Boolean is_active = true;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @Column(name = "student_code", length = 20)
    private String student_code;

    @Column(length = 100)
    private String faculty;

    @Column(length = 100)
    private String major;

    private LocalDate birth_date;

    @Column(name = "phone_number", length = 20)
    private String phone_number;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // LOẠI TRỪ
    private Set<EventRegistration> eventRegistrations = new HashSet<>();
}