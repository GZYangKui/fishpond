package cn.navclub.fishpond.server.service.impl

import cn.navclub.fishpond.core.config.Constant
import cn.navclub.fishpond.protocol.api.CommonResult
import cn.navclub.fishpond.server.service.BaseService
import cn.navclub.fishpond.server.service.FileService
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.UploadObjectArgs
import io.minio.messages.Bucket
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

class FileServiceImpl(vertx: Vertx, config: JsonObject) : FileService, BaseService(vertx, config) {
    private val client: MinioClient

    init {
        this.client = MinioClient
            .builder()
            .endpoint(this.getJsonFile<String>(Constant.MINIO, Constant.ENDPOINT))
            .credentials(
                this.getJsonFile(Constant.MINIO, Constant.ACCESS_KEY),
                this.getJsonFile(Constant.MINIO, Constant.ACCESS_SECRET)
            )
            .build()
    }

    override suspend fun upload(ctx: RoutingContext): CommonResult<List<String>> {
        val files = ctx.fileUploads()

        if (files.isEmpty()) {
            return CommonResult.fail("至少上传一个文件!")
        }

        try {
            val list: MutableList<String> = arrayListOf()
            val dateStr = SimpleDateFormat("yyyy-MM-dd").format(Date())
            for (file in files) {
                val filename = "$dateStr${File.separator}${file.fileName()}"
                val args = UploadObjectArgs
                    .builder()
                    .`object`(filename)
                    .bucket(this.getJsonFile(Constant.MINIO, Constant.BUCKET))
                    .contentType(file.contentType())
                    .filename(file.uploadedFileName())
                    .build()

                this.asyncUpload(args).await()

                list.add(getReqURI(filename))
            }
            return CommonResult.success(list)
        } finally {
            //删除上传文件
            files.forEach { Files.delete(File(it.uploadedFileName()).toPath()) }
        }

    }

    private fun getReqURI(filename: String): String {
        val bucket: String = this.getJsonFile(Constant.MINIO, Constant.BUCKET)
        val endpoint: String = this.getJsonFile(Constant.MINIO, Constant.ENDPOINT)
        return "$endpoint/$bucket/$filename"
    }

    /**
     * 异步上传文件
     */
    private fun asyncUpload(args: UploadObjectArgs): Future<String> {
        val promise = Promise.promise<String>()
        val future = CompletableFuture.supplyAsync {
            val resp = this.client.uploadObject(args)
            return@supplyAsync resp.`object`()
        }
        future.whenComplete { obj, err ->
            if (err != null) {
                promise.fail(err)
            } else {
                promise.complete(obj)
            }
        }
        return promise.future();
    }

}