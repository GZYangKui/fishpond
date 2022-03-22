package cn.navclub.fishpond.server.service

import cn.navclub.fishpond.protocol.api.CommonResult
import io.vertx.ext.web.RoutingContext

interface FileService {
    /**
     * 上传文件
     */
    suspend fun upload(ctx: RoutingContext): CommonResult<List<String>>
}