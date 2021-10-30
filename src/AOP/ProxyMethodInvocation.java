package AOP;

import AOP.interceptor.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.List;

public class ProxyMethodInvocation implements MethodInvocation{

    private List<MethodInterceptor> interceptors;
    private int idx;
    private Object originObj;
    private Method originMethod;
    private Object[] args;

    public ProxyMethodInvocation(List<MethodInterceptor> interceptors, Object originObj, Method originMethod, Object[] args) {
        this.interceptors = interceptors;
        this.originObj = originObj;
        this.originMethod = originMethod;
        this.args = args;
    }

    @Override
    public Object proceed() throws Throwable {
        if(idx >= interceptors.size()) {
            return executeOriginalMethod();
        }
        MethodInterceptor interceptor = interceptors.get(idx++);
        return interceptor.invoke(this);
    }

    private Object executeOriginalMethod() throws Throwable {
        originMethod.setAccessible(true);
        return originMethod.invoke(originObj, args);
    }
}
/**
 *    [afterItc, beforeItc]
 *
 *    afterItc.invoke(mi)
 *          Object res = mi.proceed() {
 *                  beforeItc.invoke(mi) {
 *                          execute before aspect function
 *                          return mi.proceed() {
 *                                      execution original function
 *                                 }
 *                 }
 *          }
 *          execute after aspect function
 *          return res;
 *
 *    [before, after]
 *                     idx
 *    beforeItc.invoke(mi)
 *         execute before aspect function
 *         return mi.proceed() {
 *             afterItc.invoke(mi)
 *                  Object res = mi.proceed() {
 *                        execution original function
 *                  }
 *                  execute after aspect function
 *                  return res;
 *         }
 *
 *    [afterItc1, afterItc2, beforeItc1, afterItc3, before2]
 *                                                  idx
 *    proceed()
 *    return afterItc1.invoke(mi) {
 *        Object res = mi.proceed() {
 *            return afterItc2.invoke(mi) {
 *                Object res = mi.proceed() {
 *                      return beforeItc1.invoke(mi) {
 *                          execute before1 aspect method  ~~~~~~~~
 *                          return mi.proceed() {
 *                              return afterItc3.invoke(mi) {
 *                                  Object res = mi.proceed() {
 *                                      return before2.invoke(mi) {
 *                                          execute before2 aspect   ~~~~~~~~
 *                                          return mi.proceed() {
 *                                              return res;
 *                                          }
 *                                      }
 *                                  }
 *                                  execute after3 aspect  ~~~~~~~~
 *                                  return res;
 *                              }
 *                          }
 *                      }
 *                }
 *                execute after2 aspect ~~~~~~~~
 *                return res;
 *            }
 *        }
 *        execute after1 aspect  ~~~~~~~~
 *        return res;
 *    }
 *    before1 ->
 *    afterItc2
 *    afterItc1
 *
 *
 *    before2 ->
 *    afterItc3
 *    afterItc2
 *    afterItc1
 */
