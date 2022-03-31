package cn.navclub.fishpond.app.task.impl;

import cn.navclub.fishpond.app.http.API;
import cn.navclub.fishpond.app.http.HTTPUtil;
import cn.navclub.fishpond.app.model.UPFileInfo;
import cn.navclub.fishpond.app.task.UDTask;
import cn.navclub.fishpond.core.config.Constant;
import cn.navclub.fishpond.core.util.StrUtil;
import cn.navclub.fishpond.protocol.api.APIECode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import net.coobird.thumbnailator.Thumbnailator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 上传任务
 */
public class UploadTask extends UDTask<List<UPFileInfo>> {
    private final File file;
    private final int width;
    private final int height;
    private final boolean picture;

    /**
     * 构造一个上传任务实例
     *
     * @param file    待上传文件
     * @param picture 待上传文件是否图片
     * @param width   生成缩略图宽度
     * @param height  生成缩略图高度
     */
    public UploadTask(final File file, boolean picture, int width, int height) {
        super(Task.UPLOAD);
        this.file = file;
        this.width = width;
        this.height = height;
        this.picture = picture;
    }

    public UploadTask(File file) {
        this(file, false, 0, 0);
    }

    @Override
    protected List<UPFileInfo> run0() throws Exception {
        var files = new ArrayList<File>();
        if (this.picture) {
            var outFile = this.getThumbnail();
            Thumbnailator.createThumbnail(file, outFile, this.width, this.height);
            files.add(0, outFile);
        }
        files.add(this.file);
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

        var formStart = String.format("------%s\r\n", formBoundary);

        //写入表单开始部分
        output.write(formStart.getBytes());
        //记录当前已发送数据量
        var send = 0;
        //计算文件总总大小
        var total = files.stream().map(File::length).count();
        var list = new ArrayList<UPFileInfo>();
        //根据RFC-2616 http1x协议拼接表单信息
        for (var i = 0; i < files.size(); i++) {
            var file = files.get(i);

            var sb = new StringBuilder();

            sb.append("Content-Disposition: form-data; name=\"file\";");
            sb.append("filename=\"").append(file.getName()).append("\"");
            sb.append("\r\n\r\n");

            output.write(sb.toString().getBytes());

            var info = new UPFileInfo();
            info.setFileSize(file.length());
            info.setFilename(file.getName());

            list.add(info);

            if (this.picture) {
                var imgInfo = new UPFileInfo.IImage();
                var preview = i == 0;
                imgInfo.setPreview(preview);
                if (preview) {
                    imgInfo.setWidth(this.width);
                    imgInfo.setHeight(this.height);
                } else {
                    //todo 获取指定图片大小
                }
            }

            //写入文件数据
            try (var input = new FileInputStream(this.file)) {
                var len = 0;
                var buffer = new byte[1024 * 16];
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                    this.onProgress(len, (send += len), total);
                }
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
            var arr = json.getJsonArray(Constant.DATA);
            for (int i = 0; i < arr.size(); i++) {
                if (i >= list.size()) {
                    break;
                }
                list.get(i).setUrl(arr.getString(i));
            }
            return list;
        }
        logger.info("文件上传失败:{}", json);
        //文件上传失败,抛出异常信息
        throw new RuntimeException(json.getString(Constant.MESSAGE));
    }

    private String getFormBoundary() {
        return "FishPondFormBoundary" + StrUtil.uuid();
    }

    private File getThumbnail() throws IOException {
        var folder = ".cache/";
        var file = new File(folder);
        if (!Files.exists(file.toPath())) {
            Files.createDirectory(file.toPath());
        }
        var suffix = "";
        var filename = this.file.getName();
        var index = filename.lastIndexOf(".");
        if (index != -1) {
            suffix = filename.substring(index);
        }
        return new File(String.format("%s%s%s", folder, StrUtil.uuid(), suffix));
    }
}

