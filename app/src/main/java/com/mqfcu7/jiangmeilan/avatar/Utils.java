package com.mqfcu7.jiangmeilan.avatar;

import android.util.Log;

import java.lang.reflect.Method;

public class Utils {
    public static final int MSG_TYPE_NONE = 0;
    public static final int MSG_TYPE_DAILY_AVATAR = 1;

    public static void invokeMethod(Object owner, String methodName, Object[] args) throws Exception {
        Class ownerClass = owner.getClass();
        Class[] argsClass = null;
        if (args != null) {
            argsClass = new Class[args.length];
            for (int i = 0; i < args.length; ++i) {
                argsClass[i] = args[i].getClass();
            }
        }

        Method method = ownerClass.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        method.invoke(owner, args);
    }
}
