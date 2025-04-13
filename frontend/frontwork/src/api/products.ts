import { axios } from '../utils/request'
import {PRODUCT_MODULE, PRODUCTS_MODULE} from './_prefix'

// 商品规格类型（根据 Specifications PO 类定义）
export type Specification = {
    item: string;   // 对应后端的 item 字段
    value: string;  // 对应后端的 value 字段
}

// 商品信息类型（对应 ProductVO）
export type ProductVO = {
    id?: number;               // 商品ID（创建时可能无ID）
    productName: string;       // 商品名称
    price: number;             // 价格
    rate?: number;             // 评分（可选）
    description: string;       // 描述
    cover: string;             // 封面图URL
    detail?: string;           // 详情（可选）
    amount: number;            // 库存数量
    frozen?: number;           // 冻结数量（可选）
    specifications: Specification[]; // 商品规格集合（数组形式，元素为无 id 的对象）
}

/**
 * 添加商品
 * @param product 商品信息（无需传递 id）
 */
export const addProduct = (product: ProductVO) => {
    return axios.post(`${PRODUCT_MODULE}/add`, product, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => res.data)
}

/**
 * 更新商品信息
 * @param product 商品信息（需包含 id）
 */
export const updateProduct = (product: ProductVO) => {
    return axios.put(`${PRODUCT_MODULE}/update`, product, {
        headers: { 'Content-Type': 'application/json' }
    }).then(res => res.data)
}

/**
 * 删除商品
 * @param id 商品ID
 */
export const deleteProduct = (id: number) => {
    return axios.delete(`${PRODUCT_MODULE}/${id}`)
        .then(res => res.data)
}

/**
 * 获取全部商品列表
 */
export const getAllProducts = () => {
    return axios.get<ProductVO[]>(`${PRODUCT_MODULE}/all`)
        .then(res => res.data)
}

/**
 * 获取单个商品详细信息
 * @param id 商品ID
 */
export const getProduct = (id: number) => {
    return axios.get<ProductVO>(`${PRODUCT_MODULE}/${id}`)
        .then(res => res.data)
}