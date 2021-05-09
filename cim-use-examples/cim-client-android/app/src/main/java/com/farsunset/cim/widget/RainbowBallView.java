/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * **************************************************************************************
 *
 *                         Website : http://www.farsunset.com                           *
 *
 * **************************************************************************************
 */
package com.farsunset.cim.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class RainbowBallView extends FrameLayout {
    private final int mWidth;
    private final int deviationRange;
    private ColorBallView yellowBall;
    private ColorBallView redBall;
    private ColorBallView blueBall;
    private ColorBallView greenBall;
    private ColorBallView pinkBall;
    private ColorBallView orangeBall;

    public RainbowBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        deviationRange = (int) (Resources.getSystem().getDisplayMetrics().density * 25);
        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    }


    @Override
    public void onFinishInflate() {
        super.onFinishInflate();



        blueBall = new ColorBallView(getContext(), (int) (- deviationRange * 2.1), (int) (mWidth / 3.5), (mWidth / 2), 0xFF6BCFF4, true);
        addView(blueBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        blueBall.setMargin(-deviationRange - deviationRange / 5, -deviationRange - deviationRange / 5);
        blueBall.setColor(0xc86BCFF4, 0xFF6BCFF4);


        redBall = new ColorBallView(getContext(), (int) (- deviationRange * 1.9), (mWidth / 7), (mWidth / 2), 0xFFFE3E47, true);
        addView(redBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        redBall.setColor(0xc8FF4C23, 0xffE21674);
        redBall.setMargin(-deviationRange , -deviationRange);


        yellowBall = new ColorBallView(getContext(), (int) (- deviationRange * 1.7), 0, (mWidth / 2), 0xFFF4C900, true);
        addView(yellowBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        yellowBall.setColor(0xc8F4C900, 0xFFF4C900);
        yellowBall.setMargin(-deviationRange + deviationRange / 5, -deviationRange);



        pinkBall = new ColorBallView(getContext(), (int)(mWidth + deviationRange * 2.1), (int) (mWidth / 3.5), (mWidth / 2), 0xFF6BCFF4, true);
        addView(pinkBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        pinkBall.setMargin(Math.abs(-deviationRange - deviationRange / 5), -deviationRange - deviationRange / 5);
        pinkBall.setColor(0xC89c27b0, 0xFf9c27b0);


        orangeBall = new ColorBallView(getContext(), (int) (mWidth + deviationRange * 1.9), (mWidth / 7), (mWidth / 2), 0xFFFE3E47, true);
        addView(orangeBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        orangeBall.setColor(0xC8ff9800, 0xFfff9800);
        orangeBall.setMargin(deviationRange , -deviationRange);


        greenBall= new ColorBallView(getContext(), (int) (mWidth + deviationRange * 1.7), 0, (mWidth / 2), 0xFFF4C900, true);
        addView(greenBall, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        greenBall.setColor(0xC808995A, 0xFf99CC66);
        greenBall.setMargin(Math.abs(-deviationRange + deviationRange / 5), -deviationRange);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int mHeight = (int) (mWidth / 3.5  + (mWidth / 2) - 2 * deviationRange);

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
    }


    public void runaway() {


        TranslateAnimation animation = new TranslateAnimation(0, -(mWidth / 2), 0, 0);
        animation.setRepeatCount(0);
        animation.setDuration(200);
        animation.setFillAfter(true);

        TranslateAnimation animation1 = new TranslateAnimation(0, -(mWidth / 2), 0, 0);
        animation1.setRepeatCount(0);
        animation1.setDuration(300);
        animation1.setFillAfter(true);

        TranslateAnimation animation2 = new TranslateAnimation(0, -(mWidth / 2), 0, 0);
        animation2.setRepeatCount(0);
        animation2.setDuration(400);
        animation2.setFillAfter(true);

        TranslateAnimation animation3 = new TranslateAnimation(0, mWidth - (mWidth / 2), 0, 0);
        animation3.setRepeatCount(0);
        animation3.setDuration(200);
        animation3.setFillAfter(true);

        TranslateAnimation animation4 = new TranslateAnimation(0, mWidth - (mWidth / 2), 0, 0);
        animation4.setRepeatCount(0);
        animation4.setDuration(300);
        animation4.setFillAfter(true);

        TranslateAnimation animation5 = new TranslateAnimation(0, mWidth - (mWidth / 2), 0, 0);
        animation5.setRepeatCount(0);
        animation5.setDuration(400);
        animation5.setFillAfter(true);


        blueBall.setAnimation(animation);
        redBall.setAnimation(animation1);
        yellowBall.setAnimation(animation2);

        orangeBall.setAnimation(animation3);
        pinkBall.setAnimation(animation4);
        greenBall.setAnimation(animation5);

        animation.start();
        animation1.start();
        animation2.start();
        animation3.start();
        animation4.start();
        animation5.start();
    }


}
