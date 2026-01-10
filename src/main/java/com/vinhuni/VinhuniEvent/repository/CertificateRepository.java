package com.vinhuni.VinhuniEvent.repository;

import com.vinhuni.VinhuniEvent.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    @Query("SELECT c FROM Certificate c WHERE c.user.userId = :userId ORDER BY c.issuedDate DESC")
    List<Certificate> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Certificate c WHERE c.event.eventId = :eventId")
    List<Certificate> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT c FROM Certificate c WHERE c.user.userId = :userId AND c.event.eventId = :eventId")
    Optional<Certificate> findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    Optional<Certificate> findByCertificateCode(String certificateCode);

    boolean existsByUserUserIdAndEventEventId(Long userId, Long eventId);
}

