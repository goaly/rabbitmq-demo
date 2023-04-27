/**
 * Copyright (C), 2022-2032
 */
package com.lys.producer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * ExceptionUtil
 *
 * @author: lys
 * @date: 2022/5/31 8:18
 */
public class ExceptionUtil {

    private ExceptionUtil() {
        // Prevent Instantiation
    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }
}
