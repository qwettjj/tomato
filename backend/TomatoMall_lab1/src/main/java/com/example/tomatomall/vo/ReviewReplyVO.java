package com.example.tomatomall.vo;

import com.example.tomatomall.po.ReviewReply;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewReplyVO {
    private String content;

    public ReviewReply toPO(){
        ReviewReply reply = new ReviewReply();
        reply.setContent(content);
        return reply;
    }
}
