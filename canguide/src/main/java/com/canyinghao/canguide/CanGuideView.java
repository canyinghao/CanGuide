package com.canyinghao.canguide;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import com.canyinghao.canguide.model.GuideBean;

import java.util.ArrayList;
import java.util.List;

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
public class CanGuideView extends FrameLayout {


    protected int mBackgroundColor = 0xCC000000;
    protected Paint mPaint;
    protected List<GuideBean> mGuides;


    protected SparseArray<View> mViews;


    public CanGuideView(Context context) {
        this(context, null);
    }

    public CanGuideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanGuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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


    public void setFilter(MaskFilter maskFilter) {

        mPaint.setMaskFilter(maskFilter);

    }


    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }


    public void setGuides(List<GuideBean> guides) {
        this.mGuides = guides;
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

    public void addGuideView(View view, int type, int round) {
        addGuideView(view, type, round, 0, 0, 0, 0, false);
    }

    public void addGuideView(View view, int type, int round, int offsetLeft, int offsetTop, int offsetRight, int offsetBottom) {
        addGuideView(view, type, round, offsetLeft, offsetTop, offsetRight, offsetBottom, false);
    }

}
