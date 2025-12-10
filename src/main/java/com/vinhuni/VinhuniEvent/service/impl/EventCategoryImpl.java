package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.EventCategory;
import com.vinhuni.VinhuniEvent.repository.EventCategoryRepository;
import com.vinhuni.VinhuniEvent.service.EventCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventCategoryImpl implements EventCategoryService {

    private final EventCategoryRepository eventCategoryRepository;

    public EventCategoryImpl(EventCategoryRepository eventCategoryRepository) {
        this.eventCategoryRepository = eventCategoryRepository;
    }

    @Override
    public List<EventCategory> getAllEventCategories() {
        return eventCategoryRepository.findAll();
    }

    @Override
    public Optional<EventCategory> getEventCategoryById(Long id) {
        // Hoàn thiện: Sử dụng repository để tìm kiếm theo ID
        return eventCategoryRepository.findById(id);
    }

    @Override
    public void saveEventCategory(EventCategory eventCategory) {
        // Hoàn thiện: Sử dụng repository để lưu đối tượng
        eventCategoryRepository.save(eventCategory);
    }

    @Override
    public void deleteEventCategory(Long id) {
        // Hoàn thiện: Sử dụng repository để xóa theo ID
        eventCategoryRepository.deleteById(id);
    }
}