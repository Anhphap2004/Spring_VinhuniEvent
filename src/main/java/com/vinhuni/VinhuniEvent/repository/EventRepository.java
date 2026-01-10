package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // --- DÀNH CHO ADMIN (Lấy tất cả) ---
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.eventRegistrations LEFT JOIN FETCH e.created_by")
    List<Event> findAllWithRegistrationsAndUser();

    @Query("SELECT e FROM Event e WHERE e.category.category_id = :categoryId")
    List<Event> findEventsByCategoryId(@Param("categoryId") Long categoryId);



    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.eventRegistrations LEFT JOIN FETCH e.created_by WHERE e.is_active = true")
    List<Event> findAllActiveEvents();


    @Query("SELECT e FROM Event e WHERE e.category.category_id = :categoryId AND e.is_active = true")
    List<Event> findActiveEventsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT a FROM Event a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Event> searchByKeyword(String keyword);
}