import { axios } from '../utils/request'
import { ACCOUNTS_MODULE } from './_prefix'

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

type UpdateInfo = {
    userName?: string
    password?: string
    address?: string
    avatar?: string
}

type accountVO = {
    id : number,
    userName: string,
    password: string,
    phone: string,
    email: string,
    address: string,
    role: string,
    createTime: string,
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
            return res.data
        })
}

// 修改用户信息接口
export const userUpdate = (updateInfo: UpdateInfo) => {
    return axios.put(`${ACCOUNTS_MODULE}/update`, updateInfo, {
        headers: { 'Content-Type': 'application/json' }
    })
        .then(res => {
            return res.data
        })
}

// 获取当前用户信息接口
export const userInfo = (token:string) => {
    return axios.get(`${ACCOUNTS_MODULE}/info`)
        .then(res => {
            return res.data
        })
}

export const getUserInfo = (id : number) => {
    return axios.get(`${ACCOUNTS_MODULE}/getUserInfo/${id}`,{
    }).then(res => {
        return res.data
    })
}

