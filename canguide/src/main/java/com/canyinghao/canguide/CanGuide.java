package com.canyinghao.canguide;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.canyinghao.candialog.CanDialogInterface;
import com.canyinghao.candialog.CanManagerDialog;
import com.canyinghao.canguide.model.GuideBean;
import com.canyinghao.canguide.model.ViewBean;

import java.util.ArrayList;
import java.util.List;

import static com.canyinghao.canguide.model.GuideRelativeType.BOTTOM;
import static com.canyinghao.canguide.model.GuideRelativeType.LEFT;
import static com.canyinghao.canguide.model.GuideRelativeType.RIGHT;
import static com.canyinghao.canguide.model.GuideRelativeType.TOP;
import static com.canyinghao.canguide.model.GuideType.CIRCLE;
import static com.canyinghao.canguide.model.GuideType.OVAL;
import static com.canyinghao.canguide.model.GuideType.RECTANGLE;
import static com.canyinghao.canguide.model.GuideType.ROUND_RECTANGLE;

/**
 * Copyright 2017 canyinghao
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CanGuide extends CanManagerDialog{

    private static String spTag = "CanGuide";

    protected int mBackgroundColor = 0xCC000000;
    protected Paint mPaint;
    protected List<GuideBean> mGuides;
    protected List<GuideBean> mGuideRelatives;
    protected List<GuideBean> mGuidePositions;
    protected List<ViewBean> mViewBeans;

    protected SparseArray<View> mViews;
    //  是否处于显示状态
    protected boolean isShowing;


    //  dialog所在的activity
    protected Activity mContext;

    //  是否可取消，为false时，点击back键也不能取消
    protected boolean mCancelable = true;


    //   消失时监听
    protected OnCanGuideListener mOnCanGuideListener;


    protected boolean isStatusBarHeight = true;
    protected boolean isHideNavigationBarHeight = false;

    protected int mLayoutId;
    protected View mLayoutView;
    protected int[] mClickViewIds;

    //  用来监听view是否测量完毕
    protected ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    protected String mUseKey;

    //   消失的点击事件
    protected OnClickListener dismissListener = new OnClickListener() {
        @Override
        public void onClick(final View view) {

            dismiss();

        }
    };

    public CanGuide(Context context) {
        this(context, null);
    }

    public CanGuide(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanGuide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = (Activity) context;
        setActivity(this.mContext);
        init();
    }

    private void init() {
        mViews = new SparseArray<>();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mPaint.setXfermode(xfermode);

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        setClickable(true);

        setWillNotDraw(false);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);
        if (mGuides != null && !mGuides.isEmpty()) {
            for (GuideBean guideBean : mGuides) {
                RectF rectF = guideBean.getRectOffset();

                if (rectF == null) {
                    continue;
                }

                switch (guideBean.type) {
                    case CIRCLE:
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(), guideBean.round == 0 ? guideBean.getMaxRadius() : guideBean.round, mPaint);
                        break;
                    case OVAL:
                        canvas.drawOval(rectF, mPaint);
                        break;
                    case ROUND_RECTANGLE:
                        canvas.drawRoundRect(rectF, guideBean.round, guideBean.round, mPaint);
                        break;
                    case RECTANGLE:
                    default:
                        canvas.drawRect(rectF, mPaint);
                        break;
                }
            }
        }
    }


    public void show() {


        if (isShowing) {
            return;
        }

        ViewGroup rootView = (ViewGroup) mContext.getWindow().getDecorView();

        if (mCancelable) {
            setOnClickListener(dismissListener);
        } else {
            setOnClickListener(null);
        }

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        rootView.addView(this, params);

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (mCancelable) {
                        dismiss();
                    }
                }
                return true;
            }
        });


        if (mLayoutId != 0) {
            mLayoutView = LayoutInflater.from(mContext).inflate(mLayoutId, this, false);
            LayoutParams paramsView = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (isStatusBarHeight) {
                paramsView.topMargin = CanGuideUtils.getStatusBarHeight(mContext);
            }
            if(!isHideNavigationBarHeight){
                paramsView.bottomMargin = CanGuideUtils.getNavigationBarHeight(mContext);
            }
            if (mClickViewIds != null&&mClickViewIds.length>0) {
                for (int viewId : mClickViewIds) {
                    mLayoutView.findViewById(viewId).setOnClickListener(dismissListener);
                }
            }

            if(mViewBeans!=null&&!mViewBeans.isEmpty()){

                for(ViewBean viewBean:mViewBeans){
                   View mView= mLayoutView.findViewById(viewBean.viewId);
                    if(mView instanceof TextView){
                        if(viewBean.resId!=0){
                            ((TextView) mView).setText(viewBean.resId);
                        }else{
                            ((TextView) mView).setText(viewBean.text);
                        }
                    }else if(mView instanceof ImageView){
                        ((ImageView) mView).setImageResource(viewBean.resId);
                    }
                }

            }

            addView(mLayoutView, paramsView);

            if (mGuideRelatives != null || mGuidePositions != null) {

                if (onGlobalLayoutListener != null) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
                    onGlobalLayoutListener = null;
                }

                onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        try {
                            if (mLayoutView.getTag() == null) {
                                mLayoutView.setTag("");

                                if (mGuideRelatives != null) {
                                    for (GuideBean bean : mGuideRelatives) {

                                        RectF rectF = bean.getRectPoint();

                                        View viewHint = mLayoutView.findViewById(bean.viewId);


                                        if(viewHint!=null){
                                            LayoutParams layoutParams;
                                            switch (bean.type) {
                                                case LEFT:

                                                    layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                    layoutParams.leftMargin = (int) rectF.left - viewHint.getMeasuredWidth() + bean.offsetLeft;
                                                    layoutParams.topMargin = bean.offsetTop;
                                                    layoutParams.bottomMargin = bean.offsetBottom;
                                                    layoutParams.rightMargin = bean.offsetRight;
                                                    viewHint.setLayoutParams(layoutParams);

                                                    break;

                                                case RIGHT:

                                                    layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                    layoutParams.leftMargin = (int) rectF.right + bean.offsetRight;
                                                    layoutParams.topMargin = bean.offsetTop;
                                                    layoutParams.bottomMargin = bean.offsetBottom;
                                                    layoutParams.rightMargin = bean.offsetRight;
                                                    viewHint.setLayoutParams(layoutParams);

                                                    break;

                                                case TOP:

                                                    layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                    layoutParams.topMargin = (int) rectF.top - viewHint.getMeasuredHeight() + bean.offsetTop;
                                                    layoutParams.leftMargin = bean.offsetLeft;
                                                    layoutParams.bottomMargin = bean.offsetBottom;
                                                    layoutParams.rightMargin = bean.offsetRight;
                                                    viewHint.setLayoutParams(layoutParams);

                                                    break;

                                                case BOTTOM:

                                                    layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                    layoutParams.topMargin = (int) rectF.bottom + bean.offsetTop;
                                                    layoutParams.leftMargin = bean.offsetLeft;
                                                    layoutParams.bottomMargin = bean.offsetBottom;
                                                    layoutParams.rightMargin = bean.offsetRight;
                                                    viewHint.setLayoutParams(layoutParams);


                                                    break;
                                            }
                                        }



                                    }
                                }


                                if (mGuidePositions != null) {

                                    for (GuideBean bean : mGuidePositions) {

                                        RectF rectF = bean.getRectPoint();

                                        View viewHint = mLayoutView.findViewById(bean.viewId);

                                        LayoutParams layoutParams = (LayoutParams) viewHint.getLayoutParams();

                                        layoutParams.width = bean.view.getWidth();
                                        layoutParams.height = bean.view.getHeight();

                                        viewHint.setX(rectF.left);
                                        viewHint.setY(rectF.top);

                                    }


                                }


                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                };

                getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);


            }

        }


        isShowing = true;

        if (!TextUtils.isEmpty(mUseKey)) {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(spTag, Activity.MODE_MULTI_PROCESS);
            sharedPreferences.edit().putBoolean(mUseKey, true).apply();
        }

        if (mOnCanGuideListener != null) {
            mOnCanGuideListener.onShow(this);
        }
    }

    /**
     * 取消
     */
    public void dismiss() {
        ViewGroup rootView = (ViewGroup) mContext.getWindow().getDecorView();
        rootView.removeView(this);
        isShowing = false;
        if (mOnCanGuideListener != null) {
            mOnCanGuideListener.onDismiss(this);
        }
        if(mOnDismissListeners!=null&&!mOnDismissListeners.isEmpty()){
            for(CanDialogInterface.OnDismissListener onDismissListener:mOnDismissListeners){
                onDismissListener.onDismiss(this);
            }
            mOnDismissListeners.clear();
        }

    }

    public void setUseKey(String useKey) {
        this.mUseKey = useKey;
    }

    public void setFilter(MaskFilter maskFilter) {

        mPaint.setMaskFilter(maskFilter);

    }

    public void setCancelable(boolean cancelable) {

        this.mCancelable = cancelable;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }


    public void setGuides(List<GuideBean> guides) {
        this.mGuides = guides;
    }


    public void setOnCanGuideListener(OnCanGuideListener onDismissListener) {

        this.mOnCanGuideListener = onDismissListener;
    }


    public void setLayoutId(int layoutId) {
        this.mLayoutId = layoutId;
    }

    public void setViewIds(int... viewIds) {
        this.mClickViewIds = viewIds;
    }


    public void addGuideView(View view, int type, int round, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom, boolean isTopStatusBar) {
        if (mGuides == null) {
            mGuides = new ArrayList<>();
        }
        GuideBean guideBean = new GuideBean(view, type, round);
        guideBean.offsetLeft = offsetLeft;
        guideBean.offsetTop = offsetTop;
        guideBean.offsetRight = offsetRight;
        guideBean.offsetBottom = offsetBottom;
        guideBean.isTopStatusBar = isTopStatusBar;
        mGuides.add(guideBean);

    }


    public void addGuideRelative(View view, int viewId, int relativeType, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom, boolean isTopStatusBar) {
        if (mGuideRelatives == null) {
            mGuideRelatives = new ArrayList<>();
        }
        GuideBean guideBean = new GuideBean(view, viewId, relativeType, 0);
        guideBean.offsetLeft = offsetLeft;
        guideBean.offsetTop = offsetTop;
        guideBean.offsetRight = offsetRight;
        guideBean.offsetBottom = offsetBottom;
        guideBean.isTopStatusBar = isTopStatusBar;
        mGuideRelatives.add(guideBean);

    }

    public void addGuidePosition(View view, int viewId, boolean isTopStatusBar) {
        if (mGuidePositions == null) {
            mGuidePositions = new ArrayList<>();
        }
        mGuidePositions.add(new GuideBean(view, viewId, isTopStatusBar));

    }


    public void addViewBean(int viewId, int resId) {
        if (mViewBeans == null) {
            mViewBeans = new ArrayList<>();
        }
        mViewBeans.add(new ViewBean(viewId, resId));

    }

    public void addViewBean(int viewId, CharSequence text) {
        if (mViewBeans == null) {
            mViewBeans = new ArrayList<>();
        }
        mViewBeans.add(new ViewBean(viewId, text));

    }



    public void setIsStatusBarHeight(boolean statusBarHeight) {
        isStatusBarHeight = statusBarHeight;
    }

    public void setHideNavigationBarHeight(boolean hideNavigationBarHeight) {
        isHideNavigationBarHeight = hideNavigationBarHeight;
    }

    public static class Builder {

        private CanGuide mGuide;


        public Builder(Activity context) {
            this(context, null);
        }

        public Builder(Activity context, String useKey) {
            boolean isShowed = false;
            if (!TextUtils.isEmpty(useKey)) {
                SharedPreferences mSharedP = context.getSharedPreferences(spTag, Activity.MODE_MULTI_PROCESS);
                isShowed = mSharedP.getBoolean(useKey, false);
            }
            if (!isShowed) {
                mGuide = new CanGuide(context);
                mGuide.setUseKey(useKey);
            }
        }


        public Builder setFilter(MaskFilter maskFilter) {
            if (mGuide != null) {
                mGuide.setFilter(maskFilter);
            }

            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            if (mGuide != null) {
                mGuide.setBackgroundColor(backgroundColor);
            }

            return this;
        }

        public Builder setGuides(List<GuideBean> guides) {
            if (mGuide != null) {
                mGuide.setGuides(guides);
            }

            return this;
        }


        public Builder setOnCanGuideListener(OnCanGuideListener onCanGuideListener) {
            if (mGuide != null) {
                mGuide.setOnCanGuideListener(onCanGuideListener);
            }

            return this;
        }


        public Builder addGuideView(View view, int type, int round, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom, boolean isTopStatusBar) {
            if (mGuide != null) {
                mGuide.addGuideView(view, type, round, offsetLeft, offsetTop, offsetRight, offsetBottom, isTopStatusBar);
            }
            return this;
        }


        public Builder addGuideView(View view, int type, int round, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {

            return addGuideView(view, type, round, offsetLeft, offsetTop, offsetRight, offsetBottom, false);
        }


        public Builder addGuideView(View view, int type, int round) {

            return addGuideView(view, type, round, 0, 0, 0, 0);
        }

        public Builder addGuideView(View view, int type) {
            return addGuideView(view, type, 0);
        }

        public Builder setLayoutId(int layoutId) {

            if (mGuide != null) {
                mGuide.setLayoutId(layoutId);
            }
            return this;
        }

        public Builder setViewPosition(View view, int viewId, int relativeType, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom, boolean isTopStatusBar) {
            if (mGuide != null) {
                mGuide.addGuideRelative(view, viewId, relativeType, offsetLeft, offsetTop, offsetRight, offsetBottom, isTopStatusBar);
            }
            return this;
        }

        public Builder setViewPosition(View view, int viewId, int relativeType, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {
            return setViewPosition(view, viewId, relativeType, offsetLeft, offsetTop, offsetRight, offsetBottom, false);
        }

        public Builder setViewPosition(View view, int viewId, int relativeType) {

            return setViewPosition(view, viewId, relativeType, 0, 0, 0, 0);


        }

        public Builder setViewAt(View view, int viewId, boolean isTopStatusBar) {
            if (mGuide != null) {
                mGuide.addGuidePosition(view, viewId, isTopStatusBar);
            }
            return this;
        }

        public Builder setViewAt(View view, int viewId) {

            return setViewAt(view, viewId, false);
        }

        public Builder setViewIds(int... viewIds) {
            if (mGuide != null) {
                mGuide.setViewIds(viewIds);
            }
            return this;
        }


        public Builder setCancelable(boolean cancelable) {

            if (mGuide != null) {
                mGuide.setCancelable(cancelable);
            }

            return this;
        }


        public Builder setIsStatusBarHeight(boolean statusBarHeight) {
            if (mGuide != null) {
                mGuide.setIsStatusBarHeight(statusBarHeight);
            }
            return this;

        }

        public Builder setHideNavigationBarHeight(boolean navigationBarHeight) {
            if (mGuide != null) {
                mGuide.setHideNavigationBarHeight(navigationBarHeight);
            }
            return this;

        }

        public Builder setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {

            if (mGuide != null) {
                mGuide.addViewBean(viewId,imageResId);
            }
            return this;
        }

        public Builder setText(@IdRes int viewId, CharSequence text) {

            if (mGuide != null) {
                mGuide.addViewBean(viewId,text);
            }

            return this;
        }


        public Builder setText(@IdRes int viewId, @StringRes int stringResId) {

            if (mGuide != null) {
                mGuide.addViewBean(viewId,stringResId);
            }

            return this;

        }



        public CanGuide show() {
            if (mGuide != null) {
                mGuide.showManager();
            }

            return mGuide;
        }


    }




    public static void initConfig(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            CanGuide.spTag = tag;
        }

    }


}
