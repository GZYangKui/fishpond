package cn.navclub.fishpond.app.task.impl;

import cn.navclub.fishpond.app.task.UDTask;

import java.io.File;

/**
 * 下载任务
 */
public class DownTask extends UDTask<File> {
    public DownTask() {
        super(Task.DOWNLOAD);
    }

    @Override
    protected File run0() throws Exception {
        return null;
    }
}
