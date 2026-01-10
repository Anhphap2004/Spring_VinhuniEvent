package com.vinhuni.VinhuniEvent.service;

import com.vinhuni.VinhuniEvent.model.About;

import java.util.List;
import java.util.Optional;

public interface AboutEventService {

    List<About> getAllEvents();

    Optional<About> getEventById(Long id);

    About saveEvent(About event);

    void deleteEvent(Long id);
}
