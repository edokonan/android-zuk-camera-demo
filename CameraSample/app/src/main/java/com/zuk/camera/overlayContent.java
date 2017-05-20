package com.zuk.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by ksymac on 2017/05/20.
 */

public class overlayContent extends View {

//    public String msg;

    //constructor
    public overlayContent(Context context) {
        super(context);

        //初期値
//        msg = "Hello Android";
    }


    public Rect rect=new Rect(100,100,100,100);                          //Rect对象


    public void setParam(int screen_width,int screen_height,
                         int preview_width,int preview_height,
                         float wrate,float hrate){

        float w_shif = (float)(screen_width - preview_width)/(float)2;
        float h_shif = (float)(screen_height - preview_height)/(float)2;

        float left = w_shif + ((float)preview_width - (float)preview_width * wrate)/2;
        float right = (float)screen_width - left;

        float top =  h_shif + ((float)preview_height - (float)preview_height * hrate)/2;
        float bottom = (float)screen_height - top;

        rect.left = (int)left;
        rect.right = (int)right;
        rect.top = (int)top;
        rect.bottom = (int)bottom;
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawColor(Color.TRANSPARENT);
        //四角形を描画
        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
        paint.setARGB(150, 0, 0, 255);                   //设置画笔颜色
        paint.setStyle(Paint.Style.STROKE);             //空心效果
        paint.setStrokeWidth((float) 3.0);              //线宽

        //描画：x1,y1,x2,y2,paint
//        canvas.drawRect(100, 100, 600, 200, paint);
        canvas.drawRect(rect,paint);
    }





}