package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.CircleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Circle;
import com.example.tomatomall.po.CircleMember;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.AccountRepository;
import com.example.tomatomall.repository.CircleMemberRepository;
import com.example.tomatomall.repository.CircleRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.AccountService;
import com.example.tomatomall.service.CircleService;
import com.example.tomatomall.service.PostService;
import com.example.tomatomall.util.SecurityUtil;
import com.example.tomatomall.vo.AccountVO;
import com.example.tomatomall.vo.CircleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CircleServiceImpl implements CircleService {
    @Autowired
    CircleRepository circleRepository;

    @Autowired
    CircleMemberRepository circleMemberRepository;

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SecurityUtil securityUtil;


    @Override
    public Boolean createCircle(CircleVO circleVO) {
        Circle circle = circleVO.toPO();
        circle.setStatus(1);
        circle.setMemberCount(0);
        circle.setCreatorId(securityUtil.getCurrentAccount().getId());
        Circle savedCircle = circleRepository.save(circle);

        if(joinCircle(savedCircle.getCircleId(), CircleEnum.OWNER)) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean joinCircle(Integer circleId, CircleEnum circleEnum) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        if(circleMemberRepository.existsByCircleIdAndAccountId(circleId, accountId)) {
            throw TomatoMallException.alreadyJoinCircle();
        }

        CircleMember circleMember = new CircleMember();
        circleMember.setCircleId(circleId);
        circleMember.setAccountId(accountId);
        circleMember.setCircleRole(circleEnum);
        circleMemberRepository.save(circleMember);

        Circle circle = circleRepository.findById(circleId).isPresent() ? circleRepository.findById(circleId).get() : null;
        if(circle == null) {
            throw TomatoMallException.circleNotExist();
        }
        circle.setMemberCount(circle.getMemberCount() + 1);
        circleRepository.save(circle);

        return true;
    }

    @Override
    public Boolean leaveCircle(Integer circleId) {
        Integer accountId = securityUtil.getCurrentAccount().getId();
        circleMemberRepository.deleteByCircleIdAndAccountId(circleId, accountId);

        Circle circle = circleRepository.findById(circleId).isPresent() ? circleRepository.findById(circleId).get() : null;
        if(circle == null) {
            throw TomatoMallException.circleNotExist();
        }
        circle.setMemberCount(circle.getMemberCount() - 1);
        circleRepository.save(circle);

        return true;
    }

    @Override
    public List<CircleVO> searchCircle(String keyword){
        return circleRepository.findByTitle(keyword).stream().map(Circle::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CircleVO> getAccountCircle(Integer AccountId){
        return circleRepository.findByCreatorId(AccountId).stream().map(Circle::toVO).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteCircle(Integer circleId) {
        Circle circle = circleRepository.findById(circleId).isPresent() ? circleRepository.findById(circleId).get() : null;
        if(circle == null) {
            throw TomatoMallException.circleNotExist();
        }
        for(Post post: postRepository.findByCircleId(circleId)) {
            postService.deletePost(post.getPostId());
        }
        circleRepository.delete(circle);
        return true;
    }

    @Override
    public CircleEnum getRole(Integer circleId) {
        Circle circle = circleRepository.findById(circleId).isPresent() ? circleRepository.findById(circleId).get() : null;
        if(circle == null) {
            throw TomatoMallException.circleNotExist();
        }
        Integer userId = securityUtil.getCurrentAccount().getId();
        CircleEnum role = null;
        if(circleMemberRepository.findByCircleIdAndAccountId(circle.getCircleId(),userId).isPresent()){
            role = circleMemberRepository.findByCircleIdAndAccountId(circle.getCircleId(),userId).get().getCircleRole();
        }else{
            role  = CircleEnum.VISITOR;
        }
        return role;
    }

    @Override
    public Boolean promoteToAdmin(Integer circleId, Integer accountId){
        if(!circleMemberRepository.existsByCircleIdAndAccountId(circleId, accountId)) {
            throw TomatoMallException.userNotInCircle();
        }

        Integer operatorId = securityUtil.getCurrentAccount().getId();
        if(!circleMemberRepository.existsByCircleIdAndAccountId(circleId, operatorId)) {
            throw TomatoMallException.operatorNotInCircle();
        }

        if(circleMemberRepository.findByCircleIdAndAccountId(circleId, operatorId).get().getCircleRole() != CircleEnum.OWNER) {
            throw TomatoMallException.noAuthority();
        }

        CircleMember cm = circleMemberRepository.findByCircleIdAndAccountId(circleId,accountId).get();
        cm.setCircleRole(CircleEnum.ADMIN);
        circleMemberRepository.save(cm);

        return true;
    }

    @Override
    public Boolean demoteAdmin(Integer circleId, Integer accountId){
        if(!circleMemberRepository.existsByCircleIdAndAccountId(circleId, accountId)) {
            throw TomatoMallException.userNotInCircle();
        }

        Integer operatorId = securityUtil.getCurrentAccount().getId();
        if(!circleMemberRepository.existsByCircleIdAndAccountId(circleId, operatorId)) {
            throw TomatoMallException.operatorNotInCircle();
        }

        if(circleMemberRepository.findByCircleIdAndAccountId(circleId, operatorId).get().getCircleRole() != CircleEnum.OWNER) {
            throw TomatoMallException.noAuthority();
        }

        if(circleMemberRepository.findByCircleIdAndAccountId(circleId, accountId).get().getCircleRole() != CircleEnum.ADMIN) {
            throw TomatoMallException.isNotAdmin();
        }

        CircleMember cm = circleMemberRepository.findByCircleIdAndAccountId(circleId,accountId).get();
        cm.setCircleRole(CircleEnum.MEMBER);
        circleMemberRepository.save(cm);

        return true;
    }

    @Override
    public List<AccountVO> getAdmins(Integer circleId){
        List<AccountVO> admins = new ArrayList<>();
        List<CircleMember> circleMembers = circleMemberRepository.findByCircleId(circleId);
        for(CircleMember circleMember : circleMembers) {
            if(circleMember.getCircleRole() == CircleEnum.ADMIN) {
                admins.add(accountRepository.findById(circleMember.getAccountId()).get().toVO());
            }
        }

        return admins;
    }


    @Override
    public List<AccountVO> getAllMembers(Integer circleId){
        List<AccountVO> members = new ArrayList<>();
        List<CircleMember> circleMembers = circleMemberRepository.findByCircleId(circleId);
        for(CircleMember circleMember : circleMembers) {
            if(circleMember.getCircleRole() == CircleEnum.MEMBER) {
                members.add(accountRepository.findById(circleMember.getAccountId()).get().toVO());
            }
        }

        return members;
    }

    @Override
    public AccountVO getOwner(Integer circleId){
        List<CircleMember> circleMembers = circleMemberRepository.findByCircleId(circleId);
        for(CircleMember circleMember : circleMembers) {
            if(circleMember.getCircleRole() == CircleEnum.OWNER) {
                return accountRepository.findById(circleMember.getAccountId()).get().toVO();
            }
        }

        return null;
    }

    @Override
    public List<CircleVO> getCircles(){
        List<Circle> circles = circleRepository.findAll();
        List<CircleVO> circleVOs = new ArrayList<>();
        for(Circle circle : circles) {
            circleVOs.add(circle.toVO());
        }

        return circleVOs;
    }

    @Override
    public CircleVO getCircle(Integer circleId){
        if(circleRepository.existsById(circleId)) {
            return circleRepository.findById(circleId).get().toVO();
        }
        else{
            throw TomatoMallException.circleNotExist();
        }
    }
}
