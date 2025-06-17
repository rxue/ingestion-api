package io.github.rxue.ingestion;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


import java.lang.reflect.Method;

public class StateLogger implements MethodInterceptor {

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("Came into interceptor from method " + method.getName());
        return methodProxy.invokeSuper(o, args);
    }
}
