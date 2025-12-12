package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Attendance;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.User;
import com.vinhuni.VinhuniEvent.repository.AttendanceRepository;
import com.vinhuni.VinhuniEvent.repository.EventRepository;
import com.vinhuni.VinhuniEvent.repository.UserRepository;
import com.vinhuni.VinhuniEvent.service.AttendanceService;
import com.vinhuni.VinhuniEvent.exception.RegistrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Attendance recordAttendance(Long eventId, Long userId, boolean isPresent) {
        // 1. Tìm kiếm bản ghi hiện tại (chỉ cập nhật trạng thái cuối cùng, không tạo log)
        Optional<Attendance> existingAttendance =
                attendanceRepository.findTopByEventIdAndUserIdOrderByAttendanceTime(eventId, userId);

        Attendance attendance;

        if (existingAttendance.isPresent()) {
            // Trường hợp 1: CẬP NHẬT bản ghi đã tồn tại (dùng logic Unique Constraint)
            attendance = existingAttendance.get();

        } else {
            // Trường hợp 2: TẠO MỚI (Nếu chưa từng điểm danh)
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new RegistrationException("Sự kiện không tồn tại."));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RegistrationException("Người dùng không tồn tại."));

            attendance = new Attendance();
            attendance.setEvent(event);
            attendance.setUser(user);
        }

        // Cập nhật trạng thái và thời gian điểm danh (luôn là thời gian hiện tại)
        attendance.setIsPresent(isPresent);
        attendance.setAttendanceTime(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Boolean> getUserAttendanceStatus(Long eventId, Long userId) {
        // Tìm bản ghi điểm danh mới nhất/duy nhất
        Optional<Attendance> latestAttendance = attendanceRepository.findTopByEventIdAndUserIdOrderByAttendanceTime(eventId, userId);

        return latestAttendance.map(Attendance::getIsPresent);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> getLatestAttendanceRecord(Long eventId, Long userId) {
        return attendanceRepository.findTopByEventIdAndUserIdOrderByAttendanceTime(eventId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendancesByEventId(Long eventId) {
        // Sử dụng phương thức JPQL JOIN FETCH
        return attendanceRepository.findByEventIdFetchingUser(eventId);
    }
}