package com.zhouj.rpc.proxy;


import javassist.*;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 基于javassist实现动态代理
 *
 * @author zhouj
 * @since 2023-03-23
 */
public class JavassistProxy {

    private static Logger log = LoggerFactory.getLogger(JavassistProxy.class);

    private static final AtomicInteger counter = new AtomicInteger(1);

    private static ConcurrentHashMap<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

    /**
     * 创建动态代理类
     *
     * @param classLoader
     * @param targetClass
     * @param handler
     * @return
     * @throws Exception
     */
    public static Object newProxyInstance(ClassLoader classLoader, Class<?> targetClass, InvocationHandler handler) throws Exception {


        if (proxyCache.containsKey(targetClass)) {
            return proxyCache.get(targetClass);
        }
        ClassPool pool = ClassPool.getDefault();

        //生成代理类的全限定名
        String proxyName = generateClassName(targetClass);
        // 创建代理类
        CtClass proxy = pool.makeClass(proxyName);

        List<Method> methods = new ArrayList<>();

        //接口方法列表
        CtField methodsField = null;
        try {
            methodsField = CtField.make("public static java.util.List methods;", proxy);
        } catch (CannotCompileException e) {
            log.error(e.getMessage(), e);
        }
        proxy.addField(methodsField);

        CtField handlerField = CtField.make("private " + InvocationHandler.class.getName() + " handler;", proxy);
        proxy.addField(handlerField);

        CtConstructor constructor = new CtConstructor(new CtClass[]{pool.get(InvocationHandler.class.getName())}, proxy);
        constructor.setBody("$0.handler=$1;");
        constructor.setModifiers(Modifier.PUBLIC);
        proxy.addConstructor(constructor);

        proxy.addConstructor(CtNewConstructor.defaultConstructor(proxy));

        CtClass ctClass = pool.get(targetClass.getName());
        proxy.addInterface(ctClass);

        if (targetClass.isInterface()) {
            //基于接口生成代理类
            proxyInterface(targetClass, methods, proxy);
        } else {
            //基于类生成代理类
            List<Class<?>> classList = ClassUtils.getAllInterfaces(targetClass);
            for (Class<?> aClass : classList) {
                proxyInterface(aClass, methods, proxy);
            }
        }
        Class<?> proxyClass = proxy.toClass(classLoader, null);
        proxyClass.getField("methods").set(null, methods);
        //动态代理类写入类文件
//        proxy.writeFile();
        Object instance = proxyClass.getConstructor(InvocationHandler.class).newInstance(handler);

        Object old = proxyCache.putIfAbsent(targetClass, instance);
        if (old != null) {
            instance = old;
        }
        return instance;
    }


    /**
     * 按接口生成方法
     *
     * @param interfaceClass
     * @param methods
     * @param proxy
     * @throws Exception
     */
    private static void proxyInterface(Class<?> interfaceClass, List<Method> methods, CtClass proxy) throws Exception {

        Method[] declaredMethods = interfaceClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (!containMethod(methods, method)) {
                makeMethod(method, methods, proxy);
            }
        }

        proxy.setModifiers(Modifier.PUBLIC);
    }


    /**
     * 判断方法是否存在
     *
     * @param methods
     * @param method
     * @return
     */
    private static Boolean containMethod(List<Method> methods, Method method) {
        for (Method m : methods) {
            if (m.getName().equals(method.getName())) {
                if (m.getParameterTypes().length == method.getParameterTypes().length) {
                    Boolean flag = true;
                    for (int i = 0; i < m.getParameterTypes().length; i++) {
                        if (m.getParameterTypes()[i] != method.getParameterTypes()[i]) {
                            flag = false;
                        }
                    }
                    return flag;
                }
            }
        }
        return false;
    }

    /**
     * 构造代理方法
     *
     * @param method
     * @param methods
     * @param proxy
     * @throws CannotCompileException
     */
    private static void makeMethod(Method method, List<Method> methods, CtClass proxy) throws CannotCompileException {
        Class<?> returnType = method.getReturnType();
        Class<?>[] parameterTypes = method.getParameterTypes();

        StringBuilder methodBody = new StringBuilder("Object[] args = new Object[").append(parameterTypes.length).append("];");
        for (int j = 0; j < parameterTypes.length; j++) {
            methodBody.append(" args[").append(j).append("] = ($w)$").append(j + 1).append(";");
        }
        methodBody.append(" Object result = handler.invoke($0, (java.lang.reflect.Method)methods.get(" + methods.size() + "), args);");
        if (!Void.TYPE.equals(returnType)) {
            methodBody.append(" return ").append(returnType(returnType, "result")).append(";");
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(modifier(method.getModifiers())).append(' ').append(getParameterType(returnType)).append(' ').append(method.getName());
        stringBuilder.append('(');
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(getParameterType(parameterTypes[i]));
            stringBuilder.append(" arg").append(i);
        }
        stringBuilder.append(')');

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes != null && exceptionTypes.length > 0) {
            stringBuilder.append(" throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                if (i > 0) {
                    stringBuilder.append(',');
                }
                stringBuilder.append(getParameterType(exceptionTypes[i]));
            }
        }
        stringBuilder.append('{').append(methodBody.toString()).append('}');

        CtMethod ctMethod = CtMethod.make(stringBuilder.toString(), proxy);
        proxy.addMethod(ctMethod);

        methods.add(method);
    }


    private static String modifier(int mod) {
        if (Modifier.isPublic(mod)) {
            return "public";
        }
        if (Modifier.isProtected(mod)) {
            return "protected";
        }
        if (Modifier.isPrivate(mod)) {
            return "private";
        }
        return "";
    }

    /**
     * 数组类型返回 String[]
     *
     * @param c
     * @return
     */
    public static String getParameterType(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            } while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    private static String returnType(Class<?> cl, String name) {
        return "(" + getParameterType(cl) + ")" + name;
    }

    private static String generateClassName(Class<?> type) {

        return String.format("%s$Proxy%d", type.getName(), counter.getAndIncrement());
    }
}
