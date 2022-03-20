import axios from 'axios'
import {ElMessage, ElMessageBox} from "element-plus";

// 创建axios实例
const service = axios.create({
    baseURL: "/", // api的base_url
    timeout: 15000 * 10 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(config => {
    return config
}, error => {
    // Do something with request error
    console.log(error) // for debug
    Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
    response => {
        const res = response.data;

        const code = res.code;
        //要求登录
        if (code === 401) {
            ElMessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {

            });
            return;
        }
        if (code !== 200) {
            ElMessage({
                message: res.message,
                type: 'error',
                duration: 3 * 1000
            })
            return Promise.reject(res)
        }
        return res;
    },
    error => {
        console.log('err' + error)// for debug
        ElMessage({
            message: error.message,
            type: 'error',
            duration: 3 * 1000
        })
        return Promise.reject(error)
    }
)

export default service
