import { axios } from '../utils/request'
import { HISTORY_MODULE } from './_prefix'

// 历史记录类型定义
export type HistoryVO = {
    historyId?: number;       // 历史记录ID (创建时可选)
    productId: number;        // 商品ID
    quantity: number;         // 数量
    orderId: number;          // 订单ID
    createTime?: string;      // 创建时间 (可选)
}

// 创建历史记录
export const createHistory = (historyData: HistoryVO) => {
    return axios.post(`${HISTORY_MODULE}`, historyData)
        .then(res => res.data)
}

// 删除历史记录
export const deleteHistory = (historyId: number) => {
    if (!historyId) {
        return Promise.reject(new Error('无效的历史记录ID'));
    }
    return axios.delete(`${HISTORY_MODULE}/${historyId}`)
        .then(res => res.data)
}

// 获取当前用户的历史记录列表
export const getUserHistory = () => {
    return axios.get(`${HISTORY_MODULE}/getUserHistory`)
        .then(res => res.data.data)
}

// 获取指定历史记录详情
export const getHistoryDetails = (historyId: number) => {
    return axios.get(`${HISTORY_MODULE}/${historyId}`)
        .then(res => res.data)
}

// 批量删除历史记录
export const batchDeleteHistory = (historyIds: number[]) => {
    return axios.post(`${HISTORY_MODULE}/batchDelete`, historyIds)
        .then(res => res.data)
}

// 清空当前用户的历史记录
export const clearUserHistory = () => {
    return axios.post(`${HISTORY_MODULE}/clear`)
        .then(res => res.data)
}