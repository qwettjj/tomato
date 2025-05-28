package com.example.tomatomall.service;

import com.example.tomatomall.po.History;
import com.example.tomatomall.vo.HistoryVO;

import java.util.List;

public interface HistoryService {
    public Integer createHistory(HistoryVO history);
    public boolean deleteHistory(Integer historyId);
    public List<HistoryVO> getUserHistory();
    public HistoryVO getHistoryDetail(Integer historyId);
    public boolean batchDeleteHistory(List<Integer> historyIds);
    public boolean clearUserHistory();
}
