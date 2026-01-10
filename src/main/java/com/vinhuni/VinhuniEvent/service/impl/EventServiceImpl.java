package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.repository.EventRepository;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repo;

    public EventServiceImpl(EventRepository repo) {
        this.repo = repo;
    }

    // --- CÁC HÀM CHO ADMIN (Xem hết) ---
    @Override
    public List<Event> getEventsByCategoryId(Long categoryId) {
        return repo.findEventsByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return repo.findAllWithRegistrationsAndUser();
    }

    // --- CÁC HÀM CHO USER (Chỉ xem Active) ---
    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllActiveEvents() {
        // Gọi hàm repository có điều kiện WHERE is_active = true
        return repo.findAllActiveEvents();
    }

    @Override
    public List<Event> getActiveEventsByCategoryId(Long categoryId) {
        // Gọi hàm repository có điều kiện WHERE is_active = true
        return repo.findActiveEventsByCategoryId(categoryId);
    }
    // ------------------------------------------

    @Override
    public Optional<Event> getEventById(Long id) {
        return repo.findById(id);
    }

    @Override
    public void saveEvent(Event event) {
        repo.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<Event> searchEvents(String keyword) {
        if (keyword != null) {
            return repo.searchByKeyword(keyword);
        }
        return repo.findAll();
    }


}