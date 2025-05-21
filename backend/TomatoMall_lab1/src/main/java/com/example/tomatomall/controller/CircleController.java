package com.example.tomatomall.controller;

import com.example.tomatomall.enums.CircleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.CircleMember;
import com.example.tomatomall.service.CircleService;
import com.example.tomatomall.vo.AccountVO;
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
        try{
            return Response.buildSuccess(circleService.createCircle(circleVO));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }

    }

    @GetMapping("/search")
    public Response<List<CircleVO>> searchCircle(@RequestParam String keyword) {
        try{
            return Response.buildSuccess(circleService.searchCircle(keyword));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/{circleId}/join")
    public Response<Boolean> joinCircle(@PathVariable Integer circleId, @RequestAttribute Integer accountId) {
        try{
            return Response.buildSuccess(circleService.joinCircle(circleId, accountId, CircleEnum.MEMBER));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/{circleId}/leave")
    public Response<Boolean> leaveCircle(@PathVariable Integer circleId, @RequestAttribute Integer accountId) {
        try{
            return Response.buildSuccess(circleService.leaveCircle(circleId, accountId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/{circleId}/delete")
    public Response<Boolean> deleteCircle(@PathVariable Integer circleId) {
        try{
            return Response.buildSuccess(circleService.deleteCircle(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/{accountId}/get")
    public Response<List<CircleVO>> getCircle(@PathVariable Integer accountId) {
        try{
            return Response.buildSuccess(circleService.getAccountCircle(accountId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/getRole")
    public Response<CircleEnum> getRole(@RequestParam Integer circleId) {
        try{
            return Response.buildSuccess(circleService.getRole(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/{circleId}/promote")
    public Response<Boolean> promoteToAdmin(@PathVariable Integer circleId, @RequestParam Integer accountId) {
        try{
            return Response.buildSuccess(circleService.promoteToAdmin(circleId, accountId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @PostMapping("/{circleId}/demote")
    public Response<Boolean> demoteAdmin(@PathVariable Integer circleId, @RequestParam Integer accountId) {
        try{
            return Response.buildSuccess(circleService.demoteAdmin(circleId, accountId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/{circleId}/getAdmins")
    public Response<List<AccountVO>> getAdmins(@PathVariable Integer circleId) {
        try{
            return Response.buildSuccess(circleService.getAdmins(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/{circleId}/getAllMembers")
    public Response<List<AccountVO>> getMembers(@PathVariable Integer circleId) {
        try{
            return Response.buildSuccess( circleService.getAllMembers(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }

    @GetMapping("/{circleId}/getOwner")
    public Response<AccountVO> getOwner(@PathVariable Integer circleId) {
        try{
            return Response.buildSuccess(circleService.getOwner(circleId));
        }catch (TomatoMallException e){
            return Response.buildFailure(e.getMessage(),"200");
        }
    }
}
