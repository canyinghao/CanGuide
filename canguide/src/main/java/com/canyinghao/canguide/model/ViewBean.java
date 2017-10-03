package com.canyinghao.canguide.model;

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
public class ViewBean {

    public int viewId;

    public int resId;

    public CharSequence text;


    public ViewBean(int viewId, int resId) {
        this.viewId = viewId;
        this.resId = resId;
    }

    public ViewBean(int viewId, CharSequence text) {
        this.viewId = viewId;
        this.text = text;
    }

    public ViewBean() {
    }
}
