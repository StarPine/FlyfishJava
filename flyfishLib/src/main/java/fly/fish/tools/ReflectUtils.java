package fly.fish.tools;


import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具
 */
public class ReflectUtils {

    private static volatile ReflectUtils mInstance;
    private Map<String, Object> instanceMap = new HashMap<>(4);
    private Method method;
    private Object object;

    public static ReflectUtils getInstance() {
        if (mInstance == null) {
            synchronized (ReflectUtils.class) {
                if (mInstance == null) {
                    mInstance = new ReflectUtils();
                }
            }
        }
        return mInstance;
    }

    private Class<?> getPlatformClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized ReflectUtils getMethod(String className, String methodName, Class<?>... clas) {
        try {
            method = getPlatformClass(className).getMethod(methodName, clas);
            boolean containsKey = instanceMap.containsKey(className);
            if (!containsKey) {
                object = getPlatformClass(className).newInstance();
                instanceMap.put(className, object);
            } else {
                object = instanceMap.get(className);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mInstance;
    }

    public Object invoke(Object... prams) {
        if (method == null) {
            return null;
        }
        try {
            pramsLog(prams);
            return method.invoke(object, prams);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void pramsLog(Object[] prams) {
        MLog.i("method_log", "method: "+ method);
        if (prams == null)return;
        int length = prams.length;
        if (length <= 0)return;
        for (Object pram : prams) {
            if (pram instanceof String){
                MLog.i("method_log", "prams: "+(String) pram);
            }else if (pram instanceof Activity){
                MLog.i("method_log", "prams: "+(Activity) pram);
            }else if (pram instanceof Boolean){
                MLog.i("method_log", "prams: "+(Boolean) pram);
            }
        }
    }
}
