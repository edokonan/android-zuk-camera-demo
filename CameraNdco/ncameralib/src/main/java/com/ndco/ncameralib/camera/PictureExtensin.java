package com.ndco.ncameralib.camera;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.width;
import android.content.Context;

//import com.ndco.ocr.OcrConst;
//import com.ndco.ocr.OcrException;
//import com.ndco.ocr.OcrUtil;

//import org.opencv.core.Mat;
//import org.opencv.imgproc.Imgproc;

/**
 * Created by ksymac on 2017/05/27.
 */
public class PictureExtensin {
    private static final String TAG = PictureExtensin.class.getSimpleName();
    public Bitmap bitmap;
    public Point top_left_point;
    public Point top_right_point;
    public Point buttom_left_point;
    public Point buttom_right_point;

    //设置识别框的比例
//    static float overlay_rect_width_rate = 0.6f;
//    static float overlay_rect_height_rate = 0.4f;

//    public void setPicturePath(String picturePath, float overlay_rect_width_rate,float overlay_rect_height_rate
//            ,  AssetManager assetManager)    throws IOException{
        public void setPicturePath(String picturePath, float overlay_rect_width_rate,float overlay_rect_height_rate
            )    throws IOException{
        Bitmap bm = BitmapFactory.decodeFile(picturePath);
        bitmap = BitmapFactory.decodeFile(picturePath);

        //test begin
//        InputStream in =  assetManager.open("images/b03.jpg");
//        bitmap = BitmapFactory.decodeStream(in);
        //test end


        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        float top_left_x = width * (1-overlay_rect_width_rate) / (float)2;
        float top_left_y = height * (1-overlay_rect_height_rate) / (float)2;

        Log.v("top_left_x", String.valueOf(top_left_x));
        Log.v("top_left_y", String.valueOf(top_left_y));

//        top_left_point = new Point((int)top_left_x,(int)top_left_y );
//        top_right_point = new Point(width - (int)top_left_x,(int)top_left_y );
//        buttom_left_point = new Point((int)top_left_x,height - (int)top_left_y );
//        buttom_right_point = new Point(width-(int)top_left_x,height - (int)top_left_y );

//            top_left_point = new Point((int)top_left_x - 100,(int)top_left_y );
//            top_right_point = new Point(width - (int)top_left_x+100,(int)top_left_y );
//            buttom_left_point = new Point((int)top_left_x - 100,height - (int)top_left_y + 50);
//            buttom_right_point = new Point(width-(int)top_left_x+100,height - (int)top_left_y + 50);

            top_left_point = new Point((int)top_left_x,(int)top_left_y );
            top_right_point = new Point(width - (int)top_left_x,(int)top_left_y );
            buttom_left_point = new Point((int)top_left_x,height - (int)top_left_y );
            buttom_right_point = new Point(width-(int)top_left_x,height - (int)top_left_y );
    }

    //使用拍照后数据创建bitmap
    public void setBitmapWihtCameraData(byte[] data, float overlay_rect_width_rate,float overlay_rect_height_rate) {
        bitmap = BitmapFactory.decodeByteArray(data , 0, data.length);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        float top_left_x = width * (1-overlay_rect_width_rate) / (float)2;
        float top_left_y = height * (1-overlay_rect_height_rate) / (float)2;

        Log.v("top_left_x", String.valueOf(top_left_x));
        Log.v("top_left_y", String.valueOf(top_left_y));

        top_left_point = new Point((int)top_left_x,(int)top_left_y );
        top_right_point = new Point(width - (int)top_left_x,(int)top_left_y );
        buttom_left_point = new Point((int)top_left_x,height - (int)top_left_y );
        buttom_right_point = new Point(width-(int)top_left_x,height - (int)top_left_y );
    }

//    public void copyTessdataFile(String assetsName, String resourcesName, Context context, AssetManager assetManager) {
//        Log.v(TAG, "#copyTessdataFile# start");
//        long start = System.currentTimeMillis();
//        File dir = OcrUtil.getTessdataDir(context);
//        File file = new File(dir, resourcesName);
//        FileOutputStream fos = null;
//        try {
//            InputStream is = assetManager.open(assetsName + "/" + resourcesName);
//            fos = new FileOutputStream(file);
//            fos.write(OcrUtil.readAll(is));
//        }  catch (IOException e) {
//            Log.w("TAG", e.getMessage(), e);
//            throw new OcrException(OcrConst.ERROR_CODE_COPY_TESSDATA, e.getMessage(), e);
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    Log.w("TAG", e.getMessage(), e);
//                }
//            }
//        }
//        long end = System.currentTimeMillis();
//        Log.v(TAG, "#copyTessdataFile# end: (processing time = " + (end-start)/1000.0 + "s)");
//
//    }
}
