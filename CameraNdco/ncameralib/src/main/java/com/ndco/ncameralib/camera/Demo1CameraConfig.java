package com.ndco.ncameralib.camera;

/**
 * Created by ksymac on 2017/05/20.
 */

public class Demo1CameraConfig {

    //设置图片像素比例 16：9 = 1.77， 4：3 = 1.33
    public static float PicturesizeRate = 1.77f;



//    public static float priview_expand = 1.5f;
//    public static float overlay_rect_shift_height_rate = 0.9f;
    //设置识别框的比例
    public static float overlay_rect_width_rate = 0.9f;
    public static float overlay_rect_height_rate = 0.18f;


    //拍照时的对焦模式
    public static int TakePicture_FOCUS_MODE_AUTO=1;
    public static int TakePicture_FOCUS_MODE_MACRO=2;
}