package io.github.rxue.ingestion;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


import java.lang.reflect.Method;
import java.nio.file.Path;

public class StatusLogger implements MethodInterceptor {
    private static final Path STATUS_FILE_PATH = Path.of(System.getenv("CONTAINER_STATUS_FILE_PATH"));

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if ("download".equals(method.getName()) && o instanceof HttpFileDownloader httpFileDownloader) {
            System.out.println(":::::: Going to write state to status file " + STATUS_FILE_PATH);
            System.out.println("::::::" + httpFileDownloader.description());
        }
        return methodProxy.invokeSuper(o, args);
    }
}
