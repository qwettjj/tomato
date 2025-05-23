import { axios } from '../utils/request'
import { CIRCLE_MODULE } from './_prefix'

// 定义类型
export type CircleVO = {
    id : number;
    title: string;
    description: string;
    cover: string;
    status: number;
    creatorId: number;
}

// 创建圈子
export const createCircle = (circleData: CircleVO) => {
    return axios.post(`${CIRCLE_MODULE}`, circleData)
        .then(res => res.data)
}

// 搜索圈子
export const searchCircles = (keyword: string) => {
    return axios.get(`${CIRCLE_MODULE}/search`, {
        params: { keyword }
    }).then(res => res.data)
}

// 加入圈子（accountId 通过请求属性传递，无需前端显式传参）
export const joinCircle = (circleId: number) => {
    return axios.post(`${CIRCLE_MODULE}/${circleId}/join`)
        .then(res => res.data)
}

// 退出圈子
export const leaveCircle = (circleId: number) => {
    return axios.post(`${CIRCLE_MODULE}/${circleId}/leave`)
        .then(res => res.data)
}

// 删除圈子
export const deleteCircle = (circleId: number) => {
    return axios.post(`${CIRCLE_MODULE}/${circleId}/delete`)
        .then(res => res.data)
}

// 获取用户加入的圈子列表
export const getUserCircles = (accountId: number) => {
    return axios.get(`${CIRCLE_MODULE}/${accountId}/get`)
        .then(res => res.data)
}

// 获取用户在圈子中的角色
export const getCircleRole = (circleId: number) => {
    return axios.get(`${CIRCLE_MODULE}/getRole`, {
        params: { circleId }
    }).then(res => res.data)
}

// 提升为管理员
export const promoteToAdmin = (circleId: number, accountId: number) => {
    return axios.post(`${CIRCLE_MODULE}/${circleId}/promote`, null, {
        params: { accountId }
    }).then(res => res.data)
}

// 撤销管理员
export const demoteAdmin = (circleId: number, accountId: number) => {
    return axios.post(`${CIRCLE_MODULE}/${circleId}/demote`, null, {
        params: { accountId }
    }).then(res => res.data)
}

// 获取圈子管理员列表
export const getCircleAdmins = (circleId: number) => {
    return axios.get(`${CIRCLE_MODULE}/${circleId}/getAdmins`)
        .then(res => res.data)
}

// 获取圈子所有成员
export const getAllCircleMembers = (circleId: number) => {
    return axios.get(`${CIRCLE_MODULE}/${circleId}/getAllMembers`)
        .then(res => res.data.data)
}

// 获取圈子拥有者
export const getCircleOwner = (circleId: number) => {
    return axios.get(`${CIRCLE_MODULE}/${circleId}/getOwner`)
        .then(res => res.data)
}

//获取所有圈子
export const getCircles = () => {
    return axios.get(`${CIRCLE_MODULE}`)
        .then(res => res.data)
}

//获取一个圈子
export const getCircleDetails = (circleId: number) => {
    return axios.get(`${CIRCLE_MODULE}/${circleId}`)
        .then(res => res.data)
}