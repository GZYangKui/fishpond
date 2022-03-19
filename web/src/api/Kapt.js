import request from "../util/request";

export function code() {
    return request({
        url: "/api/kapt/code",
        method: 'get'
    })
}