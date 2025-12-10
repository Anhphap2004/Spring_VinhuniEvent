package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
