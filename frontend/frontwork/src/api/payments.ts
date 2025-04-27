import { axios } from '../utils/request'
import { PAYMENT_MODULE } from './_prefix'

// 发起支付宝支付（生成支付表单）
export const createAlipay = (orderId: number) => {
    return axios.get<string>(`${PAYMENT_MODULE}/pay`, {
        params: { orderId }, // 通过 URL 参数传递 orderId
        responseType: 'text' // 明确指定响应类型为纯文本（支付宝返回的是 HTML）
    }).then(res => res.data);
};

// // 支付结果通知处理（可选，通常由后端处理）
// export const handlePayNotify = () => {
//     return axios.post(`${PAYMENT_MODULE}/notify`).then(res => res.data);
// };
//
// // 支付同步返回处理
// export const handlePayReturn = () => {
//     return axios.get(`${PAYMENT_MODULE}/returnUrl`).then(res => res.data);
// };