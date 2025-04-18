import { axios } from '../utils/request'
import { ACCOUNTS_MODULE } from './_prefix'

// 登录参数类型
type LoginInfo = {
    phone: string
    password: string
}

type RegisterInfo = {
    userName: string
    password: string
    phone: string
    email: string
    address: string
    role: string
    avatar: string
}

// 前端需要提供修改这些信息的页面
type UpdateInfo = {
    userName?: string
    password?: string
    address?: string
    avatar?: string
}

// 登录接口
export const userLogin = (loginInfo: LoginInfo) => {
    return axios.post(`${ACCOUNTS_MODULE}/login`, null, {params:loginInfo })
        .then(res => {
            return res.data
        })
}


// 注册接口
export const userRegister = (registerInfo: RegisterInfo) => {
    return axios.post(`${ACCOUNTS_MODULE}/create`, registerInfo, {
        headers: { 'Content-Type': 'application/json' }
    })
        .then(res => {
            return res.data // 假设后端返回的数据在 res.data 中
        })
}

// 修改用户信息接口
export const userUpdate = (updateInfo: UpdateInfo) => {
    return axios.put(`${ACCOUNTS_MODULE}/update`, updateInfo, {
        headers: { 'Content-Type': 'application/json' }
    })
        .then(res => {
            return res.data // 假设后端返回的数据在 res.data 中
        })
}

// 获取当前用户信息接口
export const userInfo = (token:string) => {
    return axios.get(`${ACCOUNTS_MODULE}/info`,{headers: {
        Authorization: `Bearer ${token}`
    }       
    })
        .then(res => {
            return res.data // 假设后端返回的数据在 res.data 中
        })
}
