package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.About;
import com.vinhuni.VinhuniEvent.repository.AboutEventRepository;
import com.vinhuni.VinhuniEvent.service.AboutEventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AboutServiceImpl implements AboutEventService {

    private final AboutEventRepository aboutRepository;

    // Constructor Injection (CHUẨN)
    public AboutServiceImpl(AboutEventRepository aboutRepository) {
        this.aboutRepository = aboutRepository;
    }

    @Override
    public List<About> getAllEvents() {
        return aboutRepository.findAll();
    }

    @Override
    public Optional<About> getEventById(Long id) {
        return aboutRepository.findById(id);
    }

    @Override
    public About saveEvent(About about) {
        return aboutRepository.save(about);
    }
    @Override
    public void deleteEvent(Long id) {
        aboutRepository.deleteById(id);
    }
}
