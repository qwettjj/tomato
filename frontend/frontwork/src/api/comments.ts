import { axios } from '../utils/request'
import { COMMENT_MODULE } from './_prefix'

type CommentVO = {
    commentId: number,
    postId: number,
    accountId?: number,
    content: string,
    parentId?: number // 可选字段（用于回复评论）
    createTime?: string
}

// 创建评论（需同时传递请求体和查询参数）
export const createComment = (commentInfo: CommentInfo) => {
    return axios.post(`${COMMENT_MODULE}`, commentInfo, {
    }).then(res => res.data);
};

// 获取帖子评论列表
export const getPostComments = (postId: number) => {
    return axios.get(`${COMMENT_MODULE}/${postId}`)
        .then(res => res.data.data);
};

// 删除评论
export const deleteComment = (commentId: number) => {
    return axios.post(`${COMMENT_MODULE}/delete/${commentId}`)
        .then(res => res.data);
};