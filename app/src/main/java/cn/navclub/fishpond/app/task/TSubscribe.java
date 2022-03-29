package cn.navclub.fishpond.app.task;

/**
 * 任务订阅
 *
 * @param <T> 任务结果
 */
public interface TSubscribe<T> {
    /**
     *
     * 错误发生是回调该函数
     *
     * @param t 异常信息
     *
     */
    default void onError(Throwable t){

    }

    /**
     *
     * 任务进度发生改变时回调该函数
     *
     * @param delta 进度改变值
     * @param send 已发送数量
     * @param total 总进度
     */
    default void progress(long delta,long send,long total){

    }

    /**
     *
     * 任务完成回调该函数
     *
     * @param item 任务执行结果
     */
    default void complete(T item){

    }

    /**
     *
     * 任务状态改变
     *
     */
    default void statusChange(UDTask.TKStatus oldStatus,UDTask.TKStatus newStatus){

    }
}
