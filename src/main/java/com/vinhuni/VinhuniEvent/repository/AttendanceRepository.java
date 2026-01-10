package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


    @Query("SELECT a FROM Attendance a WHERE a.event.event_id = :eventId AND a.user.user_id = :userId ORDER BY a.attendanceTime DESC LIMIT 1")
    Optional<Attendance> findTopByEventIdAndUserIdOrderByAttendanceTime(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );


    @Query("SELECT a FROM Attendance a JOIN FETCH a.user WHERE a.event.event_id = :eventId ORDER BY a.attendanceTime DESC")
    List<Attendance> findByEventIdFetchingUser(@Param("eventId") Long eventId);


    @Query("SELECT a FROM Attendance a WHERE a.user.user_id = :userId")
    List<Attendance> findByUserId(@Param("userId") Long userId);
}