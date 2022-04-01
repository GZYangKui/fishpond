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
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;

import javax.imageio.ImageIO;

import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
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
            files.add(this.getThumbnail());
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

        //记录当前已发送数据量
        var send = 0;
        //计算文件总总大小
        var total = files.stream().map(File::length).count();
        var list = new ArrayList<UPFileInfo>();
        //根据RFC-2616 http1x协议拼接表单信息
        for (var i = 0; i < files.size(); i++) {
            var file = files.get(i);

            var sb = new StringBuilder();

            sb.append("------").append(formBoundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"file\";");
            sb.append("filename=\"").append(file.getName()).append("\"");
            sb.append("\r\n\r\n");

            output.write(sb.toString().getBytes());

            var info = new UPFileInfo();
            info.setFileSize(file.length());
            info.setFilename(file.getName());

            list.add(info);

            //如果是图片->初始化图片信息
            if (this.picture) {
                var dimension = this.getImageDim(file);
                var imgInfo = new UPFileInfo.IImage();

                imgInfo.setPreview(i == 0);
                imgInfo.setWidth(dimension.width);
                imgInfo.setHeight(dimension.height);

                info.setImageInfo(imgInfo);
            }

            //写入文件数据
            try (var input = new FileInputStream(file)) {
                var len = 0;
                var buffer = new byte[1024 * 16];
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                    this.onProgress(len, (send += len), total);
                }
            }
            output.write(new byte[]{'\r', '\n'});
        }

        var formETag = String.format("------%s--\r\n\r\n", formBoundary);

        //写入表单结束标示
        output.write(formETag.getBytes());

        //处理响应数据
        var respStream = connect.getInputStream();
        var json = (JsonObject) Json.decodeValue(Buffer.buffer(respStream.readAllBytes()));
        var code = json.getInteger(Constant.CODE);
        if (code == APIECode.OK.getCode()) {
            var arr = json.getJsonArray(Constant.DATA);
            for (int i = 0; i < arr.size(); i++) {
                var fileInfo = list.get(i);
                fileInfo.setUrl(arr.getString(i));
                if (fileInfo.thumbnail())
                    Files.delete(files.get(i).toPath());
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

    /**
     * 生成缩略图
     */
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
        var outFile = new File(String.format("%s%s%s", folder, StrUtil.uuid(), suffix));

        //按照指定大小缩放
        Thumbnails
                .of(this.file)
                .width(this.width)
                .height(this.height)
                .toFile(outFile);

        return outFile;
    }

    private Dimension getImageDim(File file) {
        var suffix = "";
        var filename = file.getName();
        var index = filename.lastIndexOf(".");
        if (index != -1) {
            suffix = filename.substring(index + 1);
        }
        var iter = ImageIO.getImageReadersBySuffix(suffix);
        if (!iter.hasNext()) {
            throw new RuntimeException("暂不支持:" + suffix + "格式图片!");
        }
        var reader = iter.next();
        try {
            var stream = new FileImageInputStream(file);
            reader.setInput(stream);
            var width = reader.getWidth(reader.getMinIndex());
            var height = reader.getHeight(reader.getMinIndex());
            return new Dimension(width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            reader.dispose();
        }
    }
}

