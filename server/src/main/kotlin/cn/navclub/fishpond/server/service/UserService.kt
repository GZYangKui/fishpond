package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.server.api.CommonResult

interface UserService {
    /**
     * 用户登录
     */
    suspend fun login(username:String,password:String): CommonResult<String>
}