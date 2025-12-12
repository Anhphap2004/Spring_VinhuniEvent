package com.vinhuni.VinhuniEvent.service.impl;
import com.vinhuni.VinhuniEvent.model.Event;
import com.vinhuni.VinhuniEvent.repository.EventRepository;
import com.vinhuni.VinhuniEvent.service.EventService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository repo;

    public EventServiceImpl(EventRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Event> getEventsByCategoryId(Long categoryId) {
        return repo.findEventsByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true) // Đảm bảo giao dịch đọc dữ liệu
    public List<Event> getAllEvents() {
        return repo.findAllWithRegistrationsAndUser();
    }

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


}
