package io.github.rxue.ingestion;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class StateLogger implements InvocationHandler {
    private final Object proxied;
    public StateLogger(Object proxied) {
        this.proxied = proxied;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("StateLogger Dynamic proxy invoked when invoking method " + method.getName());
        return method.invoke(proxy, args);
    }
}
