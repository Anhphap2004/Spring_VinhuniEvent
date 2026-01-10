package com.vinhuni.VinhuniEvent.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "aboutevent")
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "short_description")
    private String shortDescription;


    @Column(columnDefinition = "TEXT")
    private String content;
    // content TEXT

    @Column(length = 255)
    private String location;
    // location VARCHAR(255)

    @Column(length = 150)
    private String organizer;
    // organizer VARCHAR(150)

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    // start_time TIMESTAMP NOT NULL

    @Column(name = "end_time")
    private LocalDateTime endTime;
    // end_time TIMESTAMP

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;
    // banner_url VARCHAR(500)

    @Column(length = 50)
    private String status = "UPCOMING";
    // status VARCHAR(50)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    // created_at TIMESTAMP

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
