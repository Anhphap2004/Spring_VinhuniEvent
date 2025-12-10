package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> getAllEvents();
    List<Event> getEventsByCategoryId(Long categoryId);
    Optional<Event> getEventById(Long id);
    void saveEvent(Event event);
    void deleteEvent(Long id);
}
