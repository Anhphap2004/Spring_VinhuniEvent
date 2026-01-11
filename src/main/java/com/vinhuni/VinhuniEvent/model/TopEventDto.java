package com.vinhuni.VinhuniEvent.model;

import java.time.LocalDateTime;

public class TopEventDto {

    private String title;
    private long registrationCount;
    private LocalDateTime startTime;

    public TopEventDto(String title, long registrationCount, LocalDateTime startTime) {
        this.title = title;
        this.registrationCount = registrationCount;
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(long registrationCount) {
        this.registrationCount = registrationCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}