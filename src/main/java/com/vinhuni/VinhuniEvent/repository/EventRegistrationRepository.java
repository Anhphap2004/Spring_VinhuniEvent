package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {


    @Query("SELECT r FROM EventRegistration r WHERE r.event.event_id = :eventId AND r.user.user_id = :userId")
    Optional<EventRegistration> findByEventAndUserIds(
            @Param("eventId") Long eventId,
            @Param("userId") Long userId
    );


    @Query("SELECT r FROM EventRegistration r JOIN FETCH r.user WHERE r.event.event_id = :eventId")
    List<EventRegistration> findByEventIdFetchingUser(@Param("eventId") Long eventId);


    @Query("SELECT r FROM EventRegistration r WHERE r.user.user_id = :userId ORDER BY r.registrationDate DESC")
    List<EventRegistration> findByUserId(@Param("userId") Long userId);
}