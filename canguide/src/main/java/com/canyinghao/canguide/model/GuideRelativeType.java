package com.canyinghao.canguide.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by canyinghao on 2017/10/1.
 */

@IntDef({GuideRelativeType.LEFT, GuideRelativeType.TOP, GuideRelativeType.RIGHT,
        GuideRelativeType.BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface GuideRelativeType {


    int LEFT =0;

    int TOP =1;

    int RIGHT  =2;

    int BOTTOM =3;
}
