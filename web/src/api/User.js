import request from "../util/request";

/**
 *
 *
 * 发送注册验证码
 *
 */
export function VCode(data) {
    return request({
        url: "/api/user/VCode",
        method: 'post',
        data: data
    })
}

/**
 *
 * 用户注册
 *
 */
export function register(data) {
    return request({
        url: "/api/user/register",
        method: 'post',
        data: data
    })
}