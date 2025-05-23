package com.example.tomatomall.service;

import com.example.tomatomall.enums.CircleEnum;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.CircleVO;

import java.util.List;

public interface CircleService {
    Boolean createCircle(CircleVO circleVO);

    Boolean joinCircle(Integer circleId, Integer accountId, CircleEnum circleEnum);

    Boolean leaveCircle(Integer circleId, Integer accountId);

    List<CircleVO> searchCircle(String keyword);

    List<CircleVO> getAccountCircle(Integer accountId);

    Boolean deleteCircle(Integer circleId);

    CircleEnum getRole(Integer circleId);

    Boolean promoteToAdmin(Integer circleId, Integer accountId);

    Boolean demoteAdmin(Integer circleId, Integer accountId);

    List<AccountVO> getAdmins(Integer circleId);

    List<AccountVO> getAllMembers(Integer circleId);

    AccountVO getOwner(Integer circleId);

    List<CircleVO> getCircles();

    CircleVO getCircle(Integer circleId);
}
