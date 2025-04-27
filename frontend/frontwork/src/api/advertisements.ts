// src/api/advertisement.ts
import { axios } from '../utils/request'
import { ADVERTISEMENT_MODULE } from './_prefix'

export interface AdvertisementVO {
    advertisementId?: number
    title?: string
    content?: string
    imageUrl?: string
    productId?: string
}

/**
 * 获取所有广告
 */
export const fetchAllAdvertisements = () => {
    return axios.get<Response.ListResponse<AdvertisementVO>>(`${ADVERTISEMENT_MODULE}`)
        .then(res => res.data)
}

/**
 * 创建广告
 * @param advertisement 广告数据
 */
export const createAdvertisement = (advertisement: AdvertisementVO) => {
    return axios.post<Response.BaseResponse<boolean>>(
        `${ADVERTISEMENT_MODULE}`,
        advertisement
    ).then(res => res.data)
}

/**
 * 更新广告
 * @param advertisement 广告数据
 */
export const updateAdvertisement = (advertisement: AdvertisementVO) => {
    return axios.put<Response.BaseResponse<boolean>>(
        `${ADVERTISEMENT_MODULE}`,
        advertisement
    ).then(res => res.data)
}

/**
 * 删除广告
 * @param id 广告ID
 */
export const deleteAdvertisement = (id: number) => {
    return axios.delete<Response.BaseResponse<boolean>>(
        `${ADVERTISEMENT_MODULE}`,
        {
            params: { id }
        }
    ).then(res => res.data)
}