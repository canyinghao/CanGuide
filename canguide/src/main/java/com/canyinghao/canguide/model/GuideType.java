package com.canyinghao.canguide.model;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

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
@IntDef({GuideType.CIRCLE, GuideType.RECTANGLE, GuideType.OVAL,
        GuideType.ROUND_RECTANGLE})
@Retention(RetentionPolicy.SOURCE)
public @interface GuideType {

     int CIRCLE = 0;

     int RECTANGLE = 1;

     int OVAL=2;

     int ROUND_RECTANGLE = 3;




}
