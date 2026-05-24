package com.code5150.meshgrouptest.repository;

import com.code5150.meshgrouptest.entity.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {

    Optional<EmailData> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM EmailData e WHERE e.user.id = :userId AND e.email = :email")
    int deleteByUserIdAndEmail(@Param("userId") Long userId, @Param("email") String email);
}
