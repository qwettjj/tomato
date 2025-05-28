package com.example.tomatomall.controller;


import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.service.HistoryService;
import com.example.tomatomall.vo.HistoryVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/histories")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    @PostMapping
    public Response<Integer> createHistory(@RequestBody HistoryVO history) {
        try{
            return Response.buildSuccess(historyService.createHistory(history));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @DeleteMapping("{historyId}")
    public Response<Boolean> deleteHistory(@PathVariable Integer historyId) {
        try{
            return Response.buildSuccess(historyService.deleteHistory(historyId));
        }
        catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @GetMapping("getUserHistory")
    public Response<List<HistoryVO>> getUserHistory() {
        try{
            return Response.buildSuccess(historyService.getUserHistory());
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"400");
        }
    }

    @GetMapping("{historyId}")
    public Response<HistoryVO> getHistoryDetail(@PathVariable Integer historyId) {
        try {
            return Response.buildSuccess(historyService.getHistoryDetail(historyId));
        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    @PostMapping("/batchDelete")
    public Response<Boolean> batchDeleteHistory(@RequestBody List<Integer> historyIds) {
        try {
            return Response.buildSuccess(historyService.batchDeleteHistory(historyIds));
        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    @PostMapping("/clear")
    public Response<Boolean> clearUserHistory() {
        try {
            return Response.buildSuccess(historyService.clearUserHistory());
        } catch (TomatoMallException e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }
}
