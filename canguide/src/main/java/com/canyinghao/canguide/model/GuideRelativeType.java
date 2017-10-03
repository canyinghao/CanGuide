package com.canyinghao.canguide.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
@IntDef({GuideRelativeType.LEFT, GuideRelativeType.TOP, GuideRelativeType.RIGHT,
        GuideRelativeType.BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface GuideRelativeType {


    int LEFT =0;

    int TOP =1;

    int RIGHT  =2;

    int BOTTOM =3;
}
