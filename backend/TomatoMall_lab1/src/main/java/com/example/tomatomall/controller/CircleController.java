package com.example.tomatomall.controller;

import com.example.tomatomall.enums.CircleEnum;
import com.example.tomatomall.service.CircleService;
import com.example.tomatomall.vo.CircleVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/circles")
public class CircleController {
    @Autowired
    private CircleService circleService;

    @PostMapping
    public Response<Boolean> createCircle(@RequestBody CircleVO circleVO) {
        return Response.buildSuccess(circleService.createCircle(circleVO));
    }

    @GetMapping("/search")
    public Response<List<CircleVO>> searchCircle(@RequestParam String keyword) {
        return Response.buildSuccess(circleService.searchCircle(keyword));
    }

    @PostMapping("/{circleId}/join")
    public Response<Boolean> joinCircle(@PathVariable Integer circleId, @RequestAttribute Integer accountId) {
        return Response.buildSuccess(circleService.joinCircle(circleId, accountId, CircleEnum.MEMBER));
    }

    @PostMapping("/{circleId}/leave")
    public Response<Boolean> leaveCircle(@PathVariable Integer circleId, @RequestAttribute Integer accountId) {
        return Response.buildSuccess(circleService.leaveCircle(circleId, accountId));
    }

    @PostMapping("/{circleId}/delete")
    public Response<Boolean> deleteCircle(@PathVariable Integer circleId) {
        return Response.buildSuccess(circleService.deleteCircle(circleId));
    }

    @GetMapping("/get}")
    public Response<List<CircleVO>> getCircle(@PathVariable Integer accountId) {
        return Response.buildSuccess(circleService.getAccountCircle(accountId));
    }
}
