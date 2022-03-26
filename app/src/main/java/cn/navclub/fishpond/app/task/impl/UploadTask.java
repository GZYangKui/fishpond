package cn.navclub.fishpond.app.task.impl;

import cn.navclub.fishpond.app.http.API;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.app.task.UDTask;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.api.APIECode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * 上传任务
 */
public class UploadTask extends UDTask<String> {
    private final File file;

    public UploadTask(final File file) {
        super(Task.UPLOAD);
        this.file = file;
    }

    @Override
    protected String run0() throws Exception {

        var str = String.format("http://%s:%d%s", HTTPUtil.getHOST(), HTTPUtil.getPORT(), API.UPLOAD_FILE.getUrl());
        var url = URI.create(str).toURL();
        var formBoundary = this.getFormBoundary();
        var connect = (HttpURLConnection) url.openConnection();
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setRequestProperty("keep-alive", "true");
        connect.setRequestProperty("Content-Type", String.format("multipart/form-data;boundary=----%s", formBoundary));
        connect.setRequestProperty(Constant.SESSION_ID, HTTPUtil.getSessionId());
        connect.connect();

        var output = connect.getOutputStream();


        //根据RFC-2616 http1x协议拼接表单信息
        var sb = new StringBuilder();

        sb.append("------");
        sb.append(formBoundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"file\";");
        sb.append("filename=\"").append(file.getName()).append("\"");
        sb.append("\r\n\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");

        output.write(sb.toString().getBytes());

        //写入文件数据
        try (var input = new FileInputStream(this.file)) {
            var buffer = new byte[1024 * 16];
            var len = 0;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
        }

        var formETag = String.format("\r\n------%s--\r\n\r\n", formBoundary);

        //写入表单结束标示
        output.write(formETag.getBytes());

        //处理响应数据
        var respStream = connect.getInputStream();
        var json = (JsonObject) Json.decodeValue(Buffer.buffer(respStream.readAllBytes()));
        var code = json.getInteger(Constant.CODE);
        if (code == APIECode.OK.getCode()) {
            return json.getString(Constant.DATA);
        }
        logger.info("文件上传失败:{}", json);
        //文件上传失败,抛出异常信息
        throw new RuntimeException(json.getString(Constant.MESSAGE));
    }

    private String getFormBoundary() {
        return "FishPondFormBoundary" + StrUtil.uuid();
    }
}

