package AOP;


import AOP.advice.*;
import AOP.interceptor.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdkAOPInvocationHandler implements InvocationHandler {

    private Object originObj;
    private Object aspectObj;

    public JdkAOPInvocationHandler(Object originObj, Object aspectObj) {
        this.originObj = originObj;
        this.aspectObj = aspectObj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> aspectClass = aspectObj.getClass();
        List<MethodInterceptor> interceptors = new ArrayList<>();
        for(Method aspectMethod: aspectClass.getDeclaredMethods()) {
            for(Annotation ano: aspectMethod.getDeclaredAnnotations()) {
                MethodInterceptor methodInterceptor = null;
                if(ano.annotationType() == Before.class) {
                    methodInterceptor = new BeforeMethodInterceptor(aspectObj, aspectMethod);
                } else if(ano.annotationType() == After.class) {
                    methodInterceptor = new AfterMethodInterceptor(aspectObj, aspectMethod);
                } else if(ano.annotationType() == Around.class) {
                    methodInterceptor = new AroundMethodInterceptor(aspectObj, aspectMethod);
                } else if (ano.annotationType() == PointCut.class) {
                    methodInterceptor = new PointCutMethodInterceptor(aspectObj, aspectMethod);
                } else if (ano.annotationType() == AfterReturn.class) {
                    methodInterceptor = new AfterReturnMethodInterceptor(aspectObj, aspectMethod);
                } else if (ano.annotationType() == AfterThrow.class) {
                    methodInterceptor = new AfterThrowMethodInterceptor(aspectObj, aspectMethod);
                }
                interceptors.add(methodInterceptor);
            }
        }
        MethodInvocation mi = new ProxyMethodInvocation(interceptors, originObj, method, args);
        return mi.proceed();
    }
}

class AdviseSupport {
    private List<MethodInterceptor> interceptors;
    private Map<Class<?>, List<MethodInterceptor>> map;

    public AdviseSupport(Map<Class<?>, List<MethodInterceptor>> map) {
        this.map = map;
    }
}