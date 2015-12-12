package com.knight.core.exception;

/**
 * 业务逻辑校验异常， 此类异常不会进行常规的logger.error记录，一般只在前端显示提示用户
 * Date: 2015/11/20
 * Time: 13:01
 *
 * @author Rascal
 */
public class ValidationException extends BaseRuntimeException {

    private static final long serialVersionUID = -5110080598748593096L;

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
