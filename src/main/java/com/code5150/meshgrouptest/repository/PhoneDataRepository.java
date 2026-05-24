package com.code5150.meshgrouptest.repository;

import com.code5150.meshgrouptest.entity.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PhoneDataRepository extends JpaRepository<PhoneData, Long> {

    Optional<PhoneData> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Modifying
    @Query("DELETE FROM PhoneData p WHERE p.user.id = :userId AND p.phone = :phone")
    int deleteByUserIdAndPhone(@Param("userId") Long userId, @Param("phone") String phone);
}
