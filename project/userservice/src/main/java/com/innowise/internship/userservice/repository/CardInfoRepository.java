package com.innowise.internship.userservice.repository;

import com.innowise.internship.userservice.model.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    @Query(value = "SELECT * FROM card_info WHERE id in (:ids)", nativeQuery = true)
    List<CardInfo> findAllByIdIn(@Param("ids") List<Long> ids);

    Optional<CardInfo> findByNumber(String number);

    Optional<CardInfo> findByUser_Id(Long userId);
}
