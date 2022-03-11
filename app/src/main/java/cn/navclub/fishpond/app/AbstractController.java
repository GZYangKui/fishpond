package cn.navclub.fishpond.app;

import javafx.scene.Parent;

public abstract class AbstractController<T extends Parent> {
    private T parent;

    public T getParent() {
        return parent;
    }

    public void setParent(T parent) {
        this.parent = parent;
    }


    protected void dispose() {

    }
}
