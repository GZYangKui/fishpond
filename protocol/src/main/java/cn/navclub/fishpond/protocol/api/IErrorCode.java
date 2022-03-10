package cn.navclub.fishpond.protocol.api;

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
