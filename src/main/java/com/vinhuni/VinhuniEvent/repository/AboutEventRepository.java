package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.About;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AboutEventRepository
        extends JpaRepository<About, Long> {
}
