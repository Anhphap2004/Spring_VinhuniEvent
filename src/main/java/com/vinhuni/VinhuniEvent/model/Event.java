package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Cấu hình an toàn
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // BAO GỒM KHÓA CHÍNH
    private Long event_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    @EqualsAndHashCode.Exclude // LOẠI TRỪ
    private EventCategory category;

    @Column(length = 200, nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @EqualsAndHashCode.Exclude // LOẠI TRỪ
    private User created_by;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(length = 200)
    private String location;

    private Integer max_participants;

    @Column(length = 255)
    private String image;

    private Boolean is_active = true;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @Column(length = 100)
    private String slug;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // LOẠI TRỪ
    private Set<EventRegistration> eventRegistrations = new HashSet<>();
}