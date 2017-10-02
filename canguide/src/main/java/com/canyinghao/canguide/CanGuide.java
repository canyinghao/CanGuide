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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.canyinghao.canguide.model.GuideBean;
import com.canyinghao.canguide.model.GuideRelativeType;
import com.canyinghao.canguide.model.GuideType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by canyinghao on 2017/9/30.
 */

public class CanGuide extends FrameLayout {

    private static String spTag = "CanGuide";

    protected int mBackgroundColor = 0xCC000000;
    protected Paint mPaint;
    protected List<GuideBean> mGuides;
    protected List<GuideBean> mGuideRelatives;
    protected List<GuideBean> mGuidePositions;

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
                    case GuideType.CIRCLE:
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(), guideBean.round == 0 ? guideBean.getMaxRadius() : guideBean.round, mPaint);
                        break;
                    case GuideType.OVAL:
                        canvas.drawOval(rectF, mPaint);
                        break;
                    case GuideType.ROUND_RECTANGLE:
                        canvas.drawRoundRect(rectF, guideBean.round, guideBean.round, mPaint);
                        break;
                    case GuideType.RECTANGLE:
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


        if (mLayoutId != 0) {
            mLayoutView = LayoutInflater.from(mContext).inflate(mLayoutId, this, false);
            LayoutParams paramsView = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (isStatusBarHeight) {
                paramsView.topMargin = CanGuideUtils.getStatusBarHeight(mContext);
            }
            paramsView.bottomMargin = CanGuideUtils.getNavigationBarHeight(mContext);
            if (mClickViewIds != null) {
                for (int viewId : mClickViewIds) {
                    mLayoutView.findViewById(viewId).setOnClickListener(dismissListener);
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


                                        LayoutParams layoutParams;
                                        switch (bean.type) {
                                            case GuideRelativeType.LEFT:

                                                layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                layoutParams.leftMargin = (int) rectF.left - viewHint.getMeasuredWidth() + bean.offsetLeft;
                                                layoutParams.topMargin = bean.offsetTop;
                                                layoutParams.bottomMargin = bean.offsetBottom;
                                                layoutParams.rightMargin = bean.offsetRight;
                                                viewHint.setLayoutParams(layoutParams);

                                                break;

                                            case GuideRelativeType.RIGHT:

                                                layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                layoutParams.leftMargin = (int) rectF.right + bean.offsetRight;
                                                layoutParams.topMargin = bean.offsetTop;
                                                layoutParams.bottomMargin = bean.offsetBottom;
                                                layoutParams.rightMargin = bean.offsetRight;
                                                viewHint.setLayoutParams(layoutParams);

                                                break;

                                            case GuideRelativeType.TOP:

                                                layoutParams = (LayoutParams) viewHint.getLayoutParams();
                                                layoutParams.topMargin = (int) rectF.top - viewHint.getMeasuredHeight() + bean.offsetTop;
                                                layoutParams.leftMargin = bean.offsetLeft;
                                                layoutParams.bottomMargin = bean.offsetBottom;
                                                layoutParams.rightMargin = bean.offsetRight;
                                                viewHint.setLayoutParams(layoutParams);

                                                break;

                                            case GuideRelativeType.BOTTOM:

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
    protected void dismiss() {
        ViewGroup rootView = (ViewGroup) mContext.getWindow().getDecorView();
        rootView.removeView(this);
        isShowing = false;
        if (mOnCanGuideListener != null) {
            mOnCanGuideListener.onDismiss(this);
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


    public void setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {

        ImageView view = getView(viewId);
        if(view!=null){
            view.setImageResource(imageResId);
        }

    }

    public void setText(@IdRes int viewId, CharSequence text) {

        TextView view = getView(viewId);
        if(view!=null){
            view.setText(text);
        }


    }


    public void setText(@IdRes int viewId, @StringRes int stringResId) {

        TextView view = getView(viewId);
        if(view!=null){
            view.setText(stringResId);
        }



    }

    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null&&mLayoutView!=null) {
            view = mLayoutView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public void setIsStatusBarHeight(boolean statusBarHeight) {
        isStatusBarHeight = statusBarHeight;
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


        public CanGuide show() {
            if (mGuide != null) {
                mGuide.show();
            }

            return mGuide;
        }


    }


    public interface OnCanGuideListener {
        void onShow(CanGuide dialog);

        void onDismiss(CanGuide dialog);
    }


    public static void initConfig(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            CanGuide.spTag = tag;
        }

    }

}
