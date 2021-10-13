package com.epam.esm.service.util;

import com.epam.esm.service.exception.ServiceException;

import java.util.concurrent.Callable;

public class ServiceUtil {
    public static <T> T executeDaoCall(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public static void executeDaoCall(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
