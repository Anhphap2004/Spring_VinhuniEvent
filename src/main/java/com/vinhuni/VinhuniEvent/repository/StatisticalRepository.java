package com.vinhuni.VinhuniEvent.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vinhuni.VinhuniEvent.model.TopEventDto;
import com.vinhuni.VinhuniEvent.model.Attendance;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.model.EventRegistration;
import com.vinhuni.VinhuniEvent.model.User;

public interface StatisticalRepository extends JpaRepository<Event, Integer> {

    @Query("SELECT COUNT(e) FROM Event e")
    long countTotalEvents();

    @Query("SELECT COUNT(e) FROM Event e WHERE e.startTime > :now")
    long countUpcomingEvents(LocalDateTime now);

    @Query("""
        SELECT COUNT(e)
        FROM Event e
        WHERE e.startTime <= :now
          AND e.endTime >= :now
    """)
    long countOngoingEvents(LocalDateTime now);

    @Query("SELECT e FROM Event e ORDER BY e.startTime DESC")
    List<Event> findLatestEvents(Pageable pageable);

    @Query("""
    SELECT new com.vinhuni.VinhuniEvent.model.TopEventDto(
        e.title,
        COUNT(r),
        e.startTime
    )
    FROM Event e
    JOIN EventRegistration r ON r.event = e
    GROUP BY e.id, e.title, e.startTime
    ORDER BY COUNT(r) DESC, e.startTime DESC
""")
    List<TopEventDto> findTopEvents(Pageable pageable);



    @Query("SELECT COUNT(r) FROM EventRegistration r")
    long countTotalRegistrations();

    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = 1")
    long countAdmins();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = 2")
    long countStudents();

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = 3")
    long countOrganizers();

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.isPresent = true")
    long countCheckIn();

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.isPresent = false")
    long countAbsent();
}
