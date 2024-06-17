package com.sparta.lunchrecommender.domain.aop.repository;


import com.sparta.lunchrecommender.domain.aop.entity.ApiUseTime;
import com.sparta.lunchrecommender.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(User user);
}