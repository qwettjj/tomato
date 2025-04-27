import { axios } from '../utils/request'
import { ORDER_MODULE } from './_prefix'

// 新增类型定义
type GetOrderInfo = {
    id: number
}

type CreateDirectOrderInfo = {
    amount: number
    paymentMethod: string
    productId: number
    quantity: number
}

// 原有类型定义保持不变
type CreateOrderInfo = {
    totalAmount: number
    paymentMethod: string
    cartItemId: number[]
}

type UpdateOrderStatusInfo = {
    orderId: number
    status: string
}

type DeleteOrderInfo = {
    orderId: number
    cartItemId: number[]
}

type DeleteOrderDirectlyInfo = {
    orderId: number
    productId: number
    quantity: number
}

// 新增API方法
export const getOrder = (params: GetOrderInfo) => {
    return axios.get<Response<Order>>(`${ORDER_MODULE}/${params.id}`).then((res) => res.data)
}

export const createOrderDirectly = (params: CreateDirectOrderInfo) => {
    return axios.post<Response<number>>(`${ORDER_MODULE}/createDirect`, null, {
        params: {
            amount: params.amount,
            paymentMethod: params.paymentMethod,
            productId: params.productId,
            quantity : params.quantity
        }
    }).then(res => res.data)
}

export const createOrder = (params: CreateOrderInfo) => {
    return axios.post<Response<number>>(`${ORDER_MODULE}/create`, null, {
        params: {
            totalAmount: params.totalAmount,
            paymentMethod: params.paymentMethod,
            cartItemId: params.cartItemId
        },
        paramsSerializer: {
            indexes: null
        }
    }).then(res => res.data)
}

export const updateOrderStatus = (params: UpdateOrderStatusInfo) => {
    return axios.patch<Response<boolean>>(`${ORDER_MODULE}/update`, null, {
        params: {
            orderId: params.orderId,
            status: params.status
        }
    }).then(res => res.data)
}

export const deleteOrder = (params: DeleteOrderInfo) => {
    return axios.delete<Response<boolean>>(`${ORDER_MODULE}/delete`, {
        params: {
            orderId: params.orderId,
            cartItemId: params.cartItemId
        },
        paramsSerializer: {
            indexes: null
        }
    }).then(res => res.data)
}

export const deleteOrderDirectly = (params: DeleteOrderDirectlyInfo) => {
    return axios.delete<Response<boolean>>(`${ORDER_MODULE}/deleteDirect`, {
        params: {
            orderId: params.orderId,
            productId: params.productId,
            quantity: params.quantity
        },paramsSerializer: {
            indexes: null
        }
    }).then(res => res.data)
}