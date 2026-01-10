package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Attendance;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    Attendance recordAttendance(Long eventId, Long userId, boolean isPresent);
    Optional<Boolean> getUserAttendanceStatus(Long eventId, Long userId);
    List<Attendance> getAttendancesByEventId(Long eventId);
    Optional<Attendance> getLatestAttendanceRecord(Long eventId, Long userId);
    List<Attendance> getAttendancesByUserId(Long userId);

    // Thêm mới cho QR attendance
    void saveAttendance(Attendance attendance);
    Optional<Attendance> getAttendanceByEventAndUser(Long eventId, Long userId);
}