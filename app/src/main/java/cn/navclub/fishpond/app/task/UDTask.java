package cn.navclub.fishpond.app.task;


import lombok.Getter;



@Getter
public abstract class UDTask implements Runnable {
    private final Task task;

    public UDTask(Task task) {
        this.task = task;
    }


    @Override
    public void run() {
        try {
            this.run0();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void run0() throws Exception;

    public enum Task {
        /**
         * 上传
         */
        UPLOAD,
        /**
         * 下载
         */
        DOWNLOAD,
    }
}
