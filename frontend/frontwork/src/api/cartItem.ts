import { axios } from '../utils/request'
import { CART_MODULE } from './_prefix'

type CartItemInfo = {
    productId: number,
    quantity: number
}

export const addCartItem = (cartItemInfo: CartItemInfo) => {
    return axios.post(`${CART_MODULE}/add`, null, {
        params : {
            productId: cartItemInfo.productId,
            quantity: cartItemInfo.quantity
        }
    }).then(res => res.data);
};

export const removeCartItem = (productId : number) => {
    return axios.delete(`${CART_MODULE}/remove`, {  // 修正这里用反引号
        params: {
            productId
        }
    }).then(res => res.data);
}

export const removeCartItemById = (CartItemId: number) => {
    return axios.delete(`${CART_MODULE}/removeById`, {
        params: {
            CartItemId: CartItemId
        }
    }).then(res => res.data);
}

export const updateCartItem = (cartItemInfo: CartItemInfo) => {
    return axios.patch(`${CART_MODULE}/update`, null, {
        params: {
            productId: cartItemInfo.productId,
            newQuantity: cartItemInfo.quantity
        }
    }).then(res => res.data);
};

export const getCartItems = () => {
    return axios.get(`${CART_MODULE}`).then(res => res.data);
};

export const getCartItem = (id: number) => {
    return axios.get(`${CART_MODULE}/${id}`).then(res => res.data);
}

