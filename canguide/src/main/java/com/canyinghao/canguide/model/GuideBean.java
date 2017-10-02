package com.canyinghao.canguide.model;

import android.graphics.RectF;
import android.view.View;

import com.canyinghao.canguide.CanGuideUtils;

/**
 * Created by canyinghao on 2017/9/30.
 */

public class GuideBean {


    public View view;

    public int viewId;

    public int type;

    public int round;

    public int offsetLeft;

    public int offsetTop;

    public int offsetRight;

    public int offsetBottom;


    public boolean isTopStatusBar;


    public RectF rect;


    public int getMaxRadius() {
        getRectOffset();
        return rect != null ? (int) Math.max((rect.right - rect.left) / 2, (rect.bottom - rect.top) / 2) : 0;
    }

    public RectF getRectPoint() {
        RectF  rectF = CanGuideUtils.getRectByView(view);
        if(rectF!=null&&isTopStatusBar){
            rectF.top+=CanGuideUtils.getStatusBarHeight(view.getContext());
            rectF.bottom+=CanGuideUtils.getStatusBarHeight(view.getContext());
        }
        return rectF;
    }

    public RectF getRectOffset() {
        if(rect==null){
            RectF rectF = getRectPoint();
            if (rectF != null) {

                rect = new RectF(rectF.left + offsetLeft, rectF.top + offsetTop, rectF.right + offsetRight, rectF.bottom + offsetBottom);
            }
        }
        return rect;
    }

    public GuideBean() {
    }

    public GuideBean(View view, int type, int round) {
        this.view = view;
        this.type = type;
        this.round = round;
    }

    public GuideBean(View view, int viewId, int type, int round) {
        this.view = view;
        this.viewId = viewId;
        this.type = type;
        this.round = round;
    }

    public GuideBean(View view, int viewId,boolean isTopStatusBar) {
        this.view = view;
        this.viewId = viewId;
        this.isTopStatusBar = isTopStatusBar;
    }
}
