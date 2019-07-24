package com.canyinghao.canguide.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.canyinghao.canguide.CanGuide;
import com.canyinghao.canguide.CanGuideUtils;
import com.canyinghao.canguide.OnCanGuideListener;
import com.canyinghao.canguide.model.GuideRelativeType;
import com.canyinghao.canguide.model.GuideType;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by canyinghao on 2017/9/30.
 */

public class MainActivity extends AppCompatActivity {

    Button btn ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        btn = (Button) findViewById(R.id.btn);

    }



    public void click(View v){

        new CanGuide.Builder(this)
                .addGuideView(btn, GuideType.ROUND_RECTANGLE,10,20,20,-20,-20)
                .setViewPosition(btn,R.id.iv, GuideRelativeType.LEFT,0, (int) CanGuideUtils.getRectByView(btn).top,0,0)
                .setLayoutId(R.layout.main_guide)
                .setViewIds(R.id.btn_next)
                .setCancelable(true)
                .setOnCanGuideListener(new OnCanGuideListener() {
                    @Override
                    public void onShow(CanGuide dialog) {

                    }

                    @Override
                    public void onDismiss(CanGuide dialog) {

                    }
                })
                .show();


        new CanGuide.Builder(this)
                .addGuideView(btn, GuideType.CIRCLE)
                .setViewPosition(btn,R.id.iv, GuideRelativeType.LEFT,0, (int) CanGuideUtils.getRectByView(btn).top,0,0)
                .setLayoutId(R.layout.main_guide)
                .setViewIds(R.id.btn_next)
                .setCancelable(true)
                .show();

    }
}
