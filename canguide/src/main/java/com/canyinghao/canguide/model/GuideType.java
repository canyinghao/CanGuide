package com.canyinghao.canguide.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by canyinghao on 2017/9/30.
 */
@IntDef({GuideType.CIRCLE, GuideType.RECTANGLE, GuideType.OVAL,
        GuideType.ROUND_RECTANGLE})
@Retention(RetentionPolicy.SOURCE)
public @interface GuideType {

     int CIRCLE = 0;

     int RECTANGLE = 1;

     int OVAL=2;

     int ROUND_RECTANGLE = 3;




}
