package fly.fish.tools;


import android.app.Activity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具类
 */
public class ReflectUtils {

    private static volatile ReflectUtils mInstance;
    private final String TAG = "reflect_log";
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
            Class<?> clazz = Class.forName(className);
            Constructor con = clazz.getDeclaredConstructor();
            con.setAccessible(true);
            return clazz;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
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
        MLog.i(TAG, "method: "+ method);
        if (prams == null)return;
        int length = prams.length;
        if (length <= 0)return;
        for (Object pram : prams) {
            if (pram instanceof String){
                MLog.i(TAG, "prams: "+(String) pram);
            }else if (pram instanceof Activity){
                MLog.i(TAG, "prams: "+(Activity) pram);
            }else if (pram instanceof Boolean){
                MLog.i(TAG, "prams: "+(Boolean) pram);
            }
        }
    }
}
