package cn.navclub.fishpond.server.api;

public interface IErrorCode {
    /**
     *
     * Service error code
     *
     */
    int getCode();

    /**
     *
     *
     * Service error message
     *
     *
     */
    String message();
}
