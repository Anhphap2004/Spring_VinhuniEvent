package com.vinhuni.VinhuniEvent.service;


import com.vinhuni.VinhuniEvent.model.EventCategory;
import java.util.Optional;
import java.util.List;

public interface EventCategoryService {
    List<EventCategory> getAllEventCategories();
    Optional<EventCategory> getEventCategoryById(Long id);
    void saveEventCategory(EventCategory eventCategory);
    void deleteEventCategory(Long id);

}
