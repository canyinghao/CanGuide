package com.canyinghao.canguide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by canyinghao on 2017/10/1.
 */

public class CanGuideUtils {


    private static int statusBarHeight;

    /**
     * 状态栏高度
     *
     * @return int
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight != 0) {
            return statusBarHeight;
        }
        Class<?> c = null;

        Object obj = null;

        Field field = null;

        int x = 0, sbar = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            obj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(obj).toString());

            sbar = context.getResources().getDimensionPixelSize(x);
            return sbar;

        } catch (Exception e1) {

            e1.printStackTrace();

        }
        statusBarHeight = sbar;
        return sbar;

    }


    public static boolean hasNavBar(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                Resources res = context.getResources();
                int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
                if (resourceId != 0) {
                    boolean hasNav = res.getBoolean(resourceId);
                    // check override flag
                    String sNavBarOverride = getNavBarOverride();
                    if ("1".equals(sNavBarOverride)) {
                        hasNav = false;
                    } else if ("0".equals(sNavBarOverride)) {
                        hasNav = true;
                    }
                    return hasNav;
                } else { // fallback
                    return !ViewConfiguration.get(context).hasPermanentMenuKey();
                }

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return false;

    }


    /**
     * 判断虚拟按键栏是否重写
     *
     * @return String
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return sNavBarOverride;
    }


    /**
     * 获取虚拟按键栏高度
     *
     * @param context Context
     * @return int
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        try {
            if (hasNavBar(context)) {
                Resources res = context.getResources();
                int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }


    public static RectF getRectByView(View view) {

        RectF rectF = null;
        if (view != null) {
            rectF = new RectF();
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            rectF.left = location[0];
            rectF.top = location[1];
            rectF.right = location[0] + view.getWidth();
            rectF.bottom = location[1] + view.getHeight();
        }
        return rectF;

    }
}
