import {axios} from '../utils/request'
import {ACCOUNTS_MODULE} from './_prefix'

// 登录参数类型
type loginInfo = {
    phone: string
    password: string
}

type registerInfo = {
    userName : string
    password: string
    phone : string
    email : string
    address : string
    role : string
    avatar : string
}

//前端需要提供修改这些东西的页面
type updateInfo = {
    userName? : string
    password? : string
    address? : string
    avatar? : string
}

// 登录接口
export const userLogin = (loginInfo: Info) => {
    return axios.post(`${ACCOUNTS_MODULE}/login`, null, {params: Info})
        .then(res => {
            return res
        })
}

// 注册接口
export const userRegister = (registerInfo: RegisterInfo) => {
    return axios.post(`${ACCOUNTS_MODULE}/create`, registerInfo,
        {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}

//修改用户信息接口
export const userUpdate = (updateInfo : UpdateInfo) => {
    return axios.put(`${ACCOUNTS_MODULE}/update`, updateInfo, {headers: {'Content-Type': 'application/json'}})
        .then(res => {
            return res
        })
}

//获取当前用户信息接口
export const userInfo = () => {
    return axios.get(`${ACCOUNTS_MODULE}/info`)
        .then(res => {
            return res
        })
}