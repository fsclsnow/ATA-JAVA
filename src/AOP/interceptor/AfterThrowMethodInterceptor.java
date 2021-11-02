package AOP.interceptor;

import AOP.MethodInvocation;

import java.lang.reflect.Method;

public class AfterThrowMethodInterceptor implements MethodInterceptor {

    private Object aspectObj;
    private Method aspectMethod;

    public AfterThrowMethodInterceptor (Object aspectObj, Method aspectMethod){
        this.aspectObj = aspectObj;
        this.aspectMethod = aspectMethod;
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object result = mi.proceed();
        aspectMethod.setAccessible(true);
        aspectMethod.invoke(aspectObj);
        return result;
    }
}

