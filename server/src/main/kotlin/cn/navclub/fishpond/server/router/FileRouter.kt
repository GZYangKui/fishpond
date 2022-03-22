package cn.navclub.fishpond.server.router

import cn.navclub.fishpond.server.HRouter
import cn.navclub.fishpond.server.service.FileService
import cn.navclub.fishpond.server.service.impl.FileServiceImpl
import cn.navclub.fishpond.server.util.CoroutineUtil
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

class FileRouter(vertx: Vertx, config: JsonObject) : HRouter(vertx, config) {
    override fun create(router: Router) {
        val fileService: FileService = FileServiceImpl(vertx, config)

        router.post("/upload").handler {
            CoroutineUtil.restCoroutine(it) { fileService.upload(it) }
        }
    }
}