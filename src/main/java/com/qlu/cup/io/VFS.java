package com.qlu.cup.io;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 虚拟文件系统(VFS),用来读取服务器里的资源
 *
 * @author liuwenhao
 */
public abstract class VFS {
    //默认提供2个实现 JBoss6VFS,DefaultVFS
    public static final Class<?>[] IMPLEMENTATIONS = {JBoss6VFS.class, DefaultVFS.class};

    //这里是提供一个用户扩展点，可以让用户自定义VFS实现
    public static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<Class<? extends VFS>>();

    private static VFS instance;

    @SuppressWarnings("unchecked")
    public static VFS getInstance() {
        if (instance != null) {
            return instance;
        }
        List<Class<? extends VFS>> impls = new ArrayList<Class<? extends VFS>>();
        impls.addAll(USER_IMPLEMENTATIONS);
        impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));
        //遍历查找实现类，返回第一个找到的
        VFS vfs = null;
        for (int i = 0; vfs == null || !vfs.isValid(); i++) {
            Class<? extends VFS> impl = impls.get(i);
            try {
                vfs = impl.newInstance();
                vfs.isValid();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }

        VFS.instance = vfs;
        return VFS.instance;
    }

    public static void addImplClass(Class<? extends VFS> clazz) {
        if (clazz != null) {
            USER_IMPLEMENTATIONS.add(clazz);
        }
    }

    protected static Class<?> getClass(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    protected static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (SecurityException | NoSuchMethodException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T invoke(Method method, Object object, Object... parameters)
            throws IOException, RuntimeException {
        try {
            return (T) method.invoke(object, parameters);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof IOException) {
                throw (IOException) e.getTargetException();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    public abstract boolean isValid();

    protected abstract List<String> list(URL url, String forPath) throws IOException;

    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<String>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }
}
