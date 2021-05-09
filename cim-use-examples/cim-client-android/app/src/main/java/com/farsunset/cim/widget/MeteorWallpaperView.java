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
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeteorWallpaperView extends View {
    private static final int METEOR_SIZE = 7;
    private static final double METEOR_ANGEL = 45 * (Math.PI / 180);
    private static final int MOVE_RATE = 5;

    private final Paint mColorPaint;
    private final DisplayMetrics displayMetrics;
    private final List<Meteor> meteors = new ArrayList<>(METEOR_SIZE);
    private final int[] speedArray = new int[]{8,9,10,11,12,13,14,15,16};
    private final int[] colors = new int[]{0XFFFFFFFF,0XA0FFFFFF,0x00000000};
    private final float[] positions = new float[]{0F,0.03F,1F};
    private final Random random = new Random();


    public MeteorWallpaperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mColorPaint = new Paint();
        mColorPaint.setAntiAlias(true);
        mColorPaint.setStyle(Paint.Style.FILL);
        mColorPaint.setColor(0xFFFFFFFF);
        mColorPaint.setStrokeCap( Paint.Cap.ROUND );       // 线帽，即画的线条两端是否带有圆角，ROUND，圆角
        displayMetrics = Resources.getSystem().getDisplayMetrics();
        batchCreateMeteors();
    }

    private void batchCreateMeteors(){
       for (int i = 0 ;i < METEOR_SIZE;i++){
           meteors.add(createOneMeteor());
       }
        mHandler.sendEmptyMessageDelayed(0,MOVE_RATE);
    }

    public int dip2px(float dip) {
        return (int) ((Resources.getSystem().getDisplayMetrics().density * dip) + 0.5f);
    }

    private void recreateMeteor(Meteor meteor ){
        meteor.alpha = random.nextInt(180) + 75;
        meteor.length = (10 + random.nextInt(10)) * displayMetrics.widthPixels / 100;
        meteor.size = dip2px(random.nextInt(2) + 1);
        meteor.speed = speedArray[random.nextInt(speedArray.length)];
        meteor.origin = new Point();

        int deviation = meteor.length * random.nextInt(5);
        meteor.origin.x = displayMetrics.widthPixels + deviation;
        meteor.origin.y = (int) ((displayMetrics.heightPixels  * ( -20 + random.nextInt(80)) / 100F) - deviation);
        meteor.current  = meteor.origin;
    }

    private Meteor createOneMeteor(){
        Meteor meteor = new Meteor();
        recreateMeteor(meteor);
        return meteor;
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            for (Meteor meteor : meteors){
                int endX = (int) (meteor.current.x + Math.cos(METEOR_ANGEL) * meteor.length);
                int endY = (int) (meteor.current.y - Math.sin(METEOR_ANGEL) * meteor.length);
                if (endX <= 0 || endY >= displayMetrics.heightPixels){
                    recreateMeteor(meteor);
                }else {

                    meteor.current.x -= meteor.speed;
                    meteor.current.y += meteor.speed;
                }
            }
            invalidate();
        }
    };


    @Override
    public void onDraw(Canvas canvas) {
        for (Meteor meteor : meteors){
            int endX = (int) (meteor.current.x + Math.cos(METEOR_ANGEL) * meteor.length);
            int endY = (int) (meteor.current.y - Math.sin(METEOR_ANGEL) * meteor.length);
            mColorPaint.setStrokeWidth(meteor.size);
            mColorPaint.setAlpha(meteor.alpha);
            mColorPaint.setShader(new LinearGradient(meteor.current.x,meteor.current.y,endX,endY,colors,positions,Shader.TileMode.MIRROR));
            canvas.drawLine(meteor.current.x,meteor.current.y, endX,endY,mColorPaint);
        }

        mHandler.sendEmptyMessageDelayed(0,MOVE_RATE);

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    private static class Meteor{

        Point origin;
        Point current;
        int length;
        int size;
        int speed;
        int alpha;
    }

}
