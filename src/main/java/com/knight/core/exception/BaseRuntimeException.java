package com.knight.core.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Date: 2015/11/20
 * Time: 11:52
 *
 * @author Rascal
 */
public abstract class BaseRuntimeException extends NestedRuntimeException {

    private static final long serialVersionUID = -8549622303357404061L;

    private String errorCode;

    public BaseRuntimeException(String msg) {
        super(msg);
    }

    public BaseRuntimeException(String errorCode, String msg){
        super(msg);
        this.errorCode = errorCode;
    }

    public BaseRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public String getErrorCode(){
        return errorCode;
    }
}
