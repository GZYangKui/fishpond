package cn.navclub.fishpond.server.internal;

import io.vertx.core.json.JsonObject;

/**
 * 定义该数据模型用户EventBus之间传递数据
 */
public class ITModel<T> {
    private T data;
    private ITCode code;

    public ITModel(T data, ITCode code) {
        this.data = data;
        this.code = code;
    }

    public ITModel() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ITCode getCode() {
        return code;
    }

    public void setCode(ITCode code) {
        this.code = code;
    }

    public static <T> ITModel<T> create(ITCode code, T data) {
        return new ITModel<>(data, code);
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
