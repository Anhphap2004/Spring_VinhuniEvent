package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_categories")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long category_id;

    @Column(length = 100, nullable = false)
    private String category_name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean is_active = true;


    @OneToMany(mappedBy = "category")
    private List<Event> events;
}