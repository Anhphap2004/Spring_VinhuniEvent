package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.SchoolInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolInfoRepository   extends JpaRepository<SchoolInfo, Integer>
{
}
