package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.enums.CircleEnum;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Circle;
import com.example.tomatomall.po.CircleMember;
import com.example.tomatomall.po.Post;
import com.example.tomatomall.repository.CircleMemberRepository;
import com.example.tomatomall.repository.CircleRepository;
import com.example.tomatomall.repository.PostRepository;
import com.example.tomatomall.service.CircleService;
import com.example.tomatomall.service.PostService;
import com.example.tomatomall.vo.CircleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public Boolean createCircle(CircleVO circleVO) {
        Circle circle = circleVO.toPO();
        circle.setStatus(1);
        circle.setMemberCount(0);
        Circle savedCircle = circleRepository.save(circle);

        if(joinCircle(savedCircle.getCircleId(), savedCircle.getCreatorId(), CircleEnum.OWNER)) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean joinCircle(Integer circleId, Integer accountId, CircleEnum circleEnum) {
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
    public Boolean leaveCircle(Integer circleId, Integer accountId) {
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
            postService.deletePost(post.getId());
        }
        circleRepository.delete(circle);
        return true;
    }
}
