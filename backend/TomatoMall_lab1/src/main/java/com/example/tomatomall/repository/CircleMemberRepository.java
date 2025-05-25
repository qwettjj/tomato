package com.example.tomatomall.repository;

import com.example.tomatomall.po.CircleMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CircleMemberRepository extends JpaRepository<CircleMember, Integer> {
    Optional<CircleMember> findByCircleIdAndAccountId(Integer circleId, Integer accountId);
    List<CircleMember> findByCircleId(Integer circleId);
    List<CircleMember> findByAccountId(Integer accountId);
    int countByCircleId(Integer circleId);
    Boolean existsByCircleIdAndAccountId(Integer circleId, Integer accountId);
    Integer deleteByCircleIdAndAccountId(Integer circleId, Integer accountId);
}