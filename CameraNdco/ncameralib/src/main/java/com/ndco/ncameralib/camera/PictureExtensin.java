package com.ndco.ncameralib.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import static android.R.attr.width;


/**
 * Created by ksymac on 2017/05/27.
 */
public class PictureExtensin {
    public Bitmap bitmap;
    public Point top_left_point;
    public Point top_right_point;
    public Point buttom_left_point;
    public Point buttom_right_point;

    //设置识别框的比例
//    static float overlay_rect_width_rate = 0.6f;
//    static float overlay_rect_height_rate = 0.4f;

    public void setPicturePath(String picturePath, float overlay_rect_width_rate,float overlay_rect_height_rate ) {
//        Bitmap bm = BitmapFactory.decodeFile(pathName);
        bitmap = BitmapFactory.decodeFile(picturePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float top_left_x = width * (1-overlay_rect_width_rate) / (float)2;
        float top_left_y = width * (1-overlay_rect_height_rate) / (float)2;

        top_left_point = new Point((int)top_left_x,(int)top_left_y );
        top_right_point = new Point(width - (int)top_left_x,(int)top_left_y );
        buttom_left_point = new Point((int)top_left_x,height - (int)top_left_y );
        buttom_right_point = new Point(width-(int)top_left_x,height - (int)top_left_y );
    }

}
