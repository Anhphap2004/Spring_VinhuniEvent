package com.vinhuni.VinhuniEvent.model;

import java.util.List;

import com.vinhuni.VinhuniEvent.model.Event;

public class ThongKeViewModel {

    private long totalEvents;
    private long totalRegistrations;
    private long totalUsers;

    private long totalAdmins;
    private long totalStudents;
    private long totalOrganizers;

    private long upcomingEvents;
    private long ongoingEvents;

    private List<TopEventDto> topEvents;
    private List<Event> latestEvents;

    private long totalCheckIn;
    private long totalAbsent;

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalOrganizers() {
        return totalOrganizers;
    }

    public void setTotalOrganizers(long totalOrganizers) {
        this.totalOrganizers = totalOrganizers;
    }

    public long getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(long upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public long getOngoingEvents() {
        return ongoingEvents;
    }

    public void setOngoingEvents(long ongoingEvents) {
        this.ongoingEvents = ongoingEvents;
    }

    public List<TopEventDto> getTopEvents() {
        return topEvents;
    }

    public void setTopEvents(List<TopEventDto> topEvents) {
        this.topEvents = topEvents;
    }

    public List<Event> getLatestEvents() {
        return latestEvents;
    }

    public void setLatestEvents(List<Event> latestEvents) {
        this.latestEvents = latestEvents;
    }

    public long getTotalCheckIn() {
        return totalCheckIn;
    }

    public void setTotalCheckIn(long totalCheckIn) {
        this.totalCheckIn = totalCheckIn;
    }

    public long getTotalAbsent() {
        return totalAbsent;
    }

    public void setTotalAbsent(long totalAbsent) {
        this.totalAbsent = totalAbsent;
    }
}