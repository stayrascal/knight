package com.knight.core.exception;

/**
 * Date: 2015/11/20
 * Time: 12:01
 *
 * @author Rascal
 */
public class DuplicateTokenException extends BaseRuntimeException {
    public DuplicateTokenException(String msg) {
        super(msg);
    }

    public DuplicateTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
