package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", length = 50, nullable = false)
    private String roleName;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "role")
    @ToString.Exclude
    private List<User> users;
}