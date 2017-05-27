package com.ndco.ncameralib.camera;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by zuk
 */
public class UIExtensin  {

    //计算长宽比（例 1920/1080）
    static public float getRate(int width, int height) {
        float r ;
        if (width>height){
            r = (float)width / (float) height;
        }else{
            r = (float)height / (float) width;
        }
        int x = (int) (r  * 100);
        return (float)x/(float)100;
    }
    //根据相同比例选择最大的size
//    static public Camera.Size getMaxSizeByScreenSize(int width, int height, List<Camera.Size> sizeList){
//        float r = getRate(width,height);
//        for (Camera.Size size : sizeList){
//            float rate = getRate(size.width,size.height);
//            if (Math.abs(r - rate) <= 0.2) {
//                return size;
//            }
//        }
//        return sizeList.get(0);
//    }

    //根据相同比例选择最大的size
    static public Camera.Size getMaxSizeByRate(float r, List<Camera.Size> sizeList){
        for (Camera.Size size : sizeList){
            float rate = getRate(size.width,size.height);
            if (Math.abs(r - rate) <= 0.2) {
                return size;
            }
        }
        return sizeList.get(0);
    }







    static public String getSurfaceViewSize(int width, int height) {
        if (equalRate(width, height, 1.33f)) {
            return "4:3";
        } else {
            return "16:9";
        }
    }

    static public boolean equalRate(int width, int height, float rate) {
        float r = (float)width /(float) height;
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }


}
