import {axios} from "../utils/request.ts";
import {API_MODULE} from "./_prefix.ts";

export const uploadImage = (image: FormData) => {
    return axios.post(`${API_MODULE}/images`, image,
        {headers: {'Content-Type': 'multipart/form-data'}})
        .then(res => {
            return res
        })
}