package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;
// import lombok.Data; // Tạm thời comment dòng này lại để dùng thủ công cho chắc chắn
import java.util.Date;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private Integer userId;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "isread")
    private Boolean isRead = false;

    @Column(name = "createdat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return isRead;
    }

    // Quan trọng: Hàm này tên là setRead để khớp với Service
    public void setRead(Boolean read) {
        isRead = read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}