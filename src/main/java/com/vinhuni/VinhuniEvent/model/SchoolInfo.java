package com.vinhuni.VinhuniEvent.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "school_info")
@Data
public class SchoolInfo {
    @Id
    private Integer id = 1;

    private String schoolName;
    private String description;
    private String mission;
    private String vision;
    private String address;
    private String phone;
    private String email;
    private String website;

}
