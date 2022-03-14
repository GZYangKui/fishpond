package cn.navclub.fishpond.app;

import javafx.scene.Parent;

import javafx.stage.Window;

public abstract class AbstractController<T extends Parent> {
    private T parent;

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }

    /**
     *
     * 从当前组件中获取{@link Window}实例
     *
     */
    protected Window window() {
        return this.getParent().getScene().getWindow();
    }


    protected void dispose() {

    }
}
