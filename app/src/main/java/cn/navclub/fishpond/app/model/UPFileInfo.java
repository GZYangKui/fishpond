package cn.navclub.fishpond.app.model;

import lombok.Data;

/**
 * 包装上传文件信息
 */
@Data
public class UPFileInfo {
    /**
     * 文件访问路径
     */
    private String url;
    /**
     * 文件大小
     */
    private Long fileSize;
    /**
     * 文件名
     */
    private String filename;

    /**
     * 图片信息
     */
    private IImage imageInfo;


    /**
     * 判断当前图片是否缩略图
     */
    public boolean thumbnail() {
        return this.imageInfo != null && this.imageInfo.preview;
    }

    /**
     * 对于上传图片封装
     */
    @Data
    public static class IImage {
        /**
         * 图片宽度
         */
        private int width;
        /**
         * 图片高度
         */
        private int height;

        /**
         * 是否预览图片
         */
        private Boolean preview = Boolean.FALSE;
    }
}
