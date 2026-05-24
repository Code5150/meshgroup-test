package com.code5150.meshgrouptest.repository;

import com.code5150.meshgrouptest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmailsEmail(String email);

    boolean existsByPhonesPhone(String phone);
}
