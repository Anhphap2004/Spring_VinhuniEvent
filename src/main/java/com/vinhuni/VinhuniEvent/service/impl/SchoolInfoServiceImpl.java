package com.vinhuni.VinhuniEvent.service.impl;

import com.vinhuni.VinhuniEvent.model.SchoolInfo;
import com.vinhuni.VinhuniEvent.repository.SchoolInfoRepository;
import com.vinhuni.VinhuniEvent.service.SchoolInfoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class SchoolInfoServiceImpl implements SchoolInfoService {

    private final SchoolInfoRepository schoolInfoRepository;
    private static final Integer SINGLETON_ID = 1;

    public SchoolInfoServiceImpl(SchoolInfoRepository schoolInfoRepository) {
        this.schoolInfoRepository = schoolInfoRepository;
    }

    @Override
    @Cacheable(value = "schoolInfo", key = "#root.target.SINGLETON_ID")
    public SchoolInfo getSchoolInfo() {
        System.out.println("Truy vấn Database để lấy thông tin trường..."); // Để check xem cache có chạy không
        return schoolInfoRepository.findById(SINGLETON_ID).orElse(new SchoolInfo());
    }

    @Override
    @Transactional
    @CacheEvict(value = "schoolInfo", allEntries = true) // Xóa cache cũ khi cập nhật thông tin mới
    public void saveSchoolInfo(SchoolInfo schoolInfo) {
        schoolInfo.setId(SINGLETON_ID); // Ép ID luôn là 1
        schoolInfoRepository.save(schoolInfo);
    }
}