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


    // --- DÀNH CHO CLIENT/USER (Chỉ lấy is_active = true) ---

    // 1. Lấy tất cả sự kiện đang hoạt động
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.eventRegistrations LEFT JOIN FETCH e.created_by WHERE e.is_active = true")
    List<Event> findAllActiveEvents();

    // 2. Lấy sự kiện theo danh mục nhưng phải đang hoạt động
    @Query("SELECT e FROM Event e WHERE e.category.category_id = :categoryId AND e.is_active = true")
    List<Event> findActiveEventsByCategoryId(@Param("categoryId") Long categoryId);
}