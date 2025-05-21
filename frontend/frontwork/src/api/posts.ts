import { axios } from '../utils/request'
import { POST_MODULE } from './_prefix'

type PostInfo = {
    postId: number,
    accountId: number,
    circleId: number,
    createTime: number,
    likeCount: number,
    commentCount: number,
    viewCount: number,
    title: string,
    content: string
}

export const createPost = (postInfo: PostInfo) => {
    return axios.post(`${POST_MODULE}`, postInfo)
        .then(res => res.data);
};

export const getPostDetail = (postId: number) => {
    return axios.get(`${POST_MODULE}/${postId}`)
        .then(res => res.data);
};

export const getCirclePosts = (circleId: number) => {
    return axios.get(`${POST_MODULE}/circle/${circleId}`)
        .then(res => res.data);
};

export const likePost = (postId: number) => {
    return axios.post(`${POST_MODULE}/like/${postId}`)
        .then(res => res.data);
};

export const unlikePost = (postId: number) => {
    return axios.post(`${POST_MODULE}/unlike/${postId}`)
        .then(res => res.data);
};

export const deletePost = (postId: number) => {
    return axios.post(`${POST_MODULE}/delete/${postId}`)
        .then(res => res.data);
};