package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.OrderStatuEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Comment;
import com.example.tomatomall.po.History;
import com.example.tomatomall.po.Order;
import com.example.tomatomall.repository.HistoryRepository;
import com.example.tomatomall.repository.OrderRepository;
import com.example.tomatomall.service.HistoryService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.HistoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryServiceImpl implements HistoryService {
    @Autowired
    SecurityUtil securityUtil;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Integer createHistory(HistoryVO history) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        Order order = orderRepository.findById(history.getOrderId()).isPresent() ? orderRepository.findById(history.getOrderId()).get() : null;
        if(order == null) {
            throw TomatoMallException.orderNotExists();
        }

        History historyPo = history.toPo();
        historyPo.setUserId(accountId);
        historyPo.setCreateTime(LocalDateTime.now());
        return historyRepository.save(historyPo).getHistoryId();
    }

    @Override
    @Transactional
    public boolean deleteHistory(Integer historyId) {
        if(historyRepository.existsById(historyId)) {
            historyRepository.deleteById(historyId);
        }
        else{
            throw TomatoMallException.historyNotExists();
        }
        return true;
    }

    @Override
    public List<HistoryVO> getUserHistory() {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        List<History> histories = historyRepository.findByUserId(accountId);

        // 使用迭代器遍历，安全删除元素
        Iterator<History> iterator = histories.iterator();
        while (iterator.hasNext()) {
            History history = iterator.next();

            // 检查订单是否存在
            if (!orderRepository.existsById(history.getOrderId())) {
                iterator.remove(); // 使用迭代器的remove方法
                continue;
            }

            // 检查订单状态
            Order order = orderRepository.findById(history.getOrderId()).get();
            if (order.getStatus() != OrderStatuEnum.SUCCESS) {
                iterator.remove(); // 使用迭代器的remove方法
            }
        }
// 确保转换后的 VO 包含 historyId
        return histories.stream()
                .map(history -> {
                    HistoryVO vo = history.toVo();
                    // 确保 ID 被设置
                    if (vo.getHistoryId() == null) {
                        vo.setHistoryId(history.getHistoryId());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public HistoryVO getHistoryDetail(Integer historyId) {
        // 获取当前用户ID用于权限验证
        Integer currentUserId = securityUtil.getCurrentAccount().getId();

        // 查询历史记录
        History history = historyRepository.findById(historyId)
                .orElseThrow(TomatoMallException::historyNotExists);

        // 验证当前用户是否有权访问该记录
        if (!currentUserId.equals(history.getUserId())) {
            throw TomatoMallException.permissionDenied();
        }

        return history.toVo();
    }

    @Override
    public boolean batchDeleteHistory(List<Integer> historyIds) {
        if (historyIds == null || historyIds.isEmpty()) {
            return true; // 空列表直接返回成功
        }

        // 获取当前用户ID
        Integer currentUserId = securityUtil.getCurrentAccount().getId();

        // 查询所有要删除的记录并验证权限
        List<History> histories = historyRepository.findAllById(historyIds);
        if (histories.size() != historyIds.size()) {
            throw TomatoMallException.historyNotExists(); // 部分记录不存在
        }

        // 验证每条记录是否属于当前用户
        for (History history : histories) {
            if (!currentUserId.equals(history.getUserId())) {
                throw TomatoMallException.permissionDenied();
            }
        }

        // 批量删除
        historyRepository.deleteAllById(historyIds);
        return true;
    }

    @Override
    public boolean clearUserHistory() {
        // 获取当前用户ID
        Integer currentUserId = securityUtil.getCurrentAccount().getId();
        // 删除该用户的所有历史记录
        historyRepository.deleteByUserId(currentUserId);
        return true;
    }
}
