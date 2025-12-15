package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventService {
    // Dùng cho Admin
    List<Event> getAllEvents();
    List<Event> getEventsByCategoryId(Long categoryId);

    // MỚI: Dùng cho Client (User)
    List<Event> getAllActiveEvents();
    List<Event> getActiveEventsByCategoryId(Long categoryId);

    // Dùng chung
    Optional<Event> getEventById(Long id);
    void saveEvent(Event event);
    void deleteEvent(Long id);
}