package com.ndco.ncameralib.camerasample;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ndco.ncameralib.camera.UIExtensin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhukui on 2017/5/30.
 */
public class Demo2SurfacePreview extends SurfaceView implements SurfaceHolder.Callback {
    public Demo1CameraActivity myActivity;
    private static final String TAG = Demo2SurfacePreview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public Demo2SurfacePreview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated() is called");
        try {
            // Open the Camera in preview mode
            mCamera = Camera.open(0);
//            mCamera.setDisplayOrientation(90);
            mCamera.setDisplayOrientation(0);
            mCamera.setPreviewDisplay(holder);
            initCameraSizeConfig(Demo1CameraConfig.PicturesizeRate);
            mCamera.startPreview();
            resetCamera();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged() is called");
        try {
//            mCamera.startPreview();
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.cancelAutoFocus();
                    resetCamera();
                    if(success){
                        Log.d(TAG, "init onAutoFocus() is success");
                    }else{
                        Log.d(TAG, "init onAutoFocus() is failure");
                    }
                }
            });
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    //resetCamera
    private void resetCamera() {
//        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        setDispaly(mParameters, mCamera);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
    }


    //设置横屏，竖屏显示
    private void setDispaly(Camera.Parameters parameters,Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8){
            setDisplayOrientation(camera,0);//横屏时
//            setDisplayOrientation(camera,90);//竖屏时
        } else {
            parameters.setRotation(90);
        }
    }
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try{
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if(downPolymorphic!=null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        }
        catch(Exception e){
            Log.e(TAG, "image error");
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        Log.d(TAG, "surfaceDestroyed() is called");
    }

    //拍照
    Camera.PictureCallback tackpicturecallback;
    public void takePicture(Camera.PictureCallback imageCallback) {
        tackpicturecallback = imageCallback;
        if (mCamera != null){
            try {
                Log.d(TAG, "autoFocus: " );
                mCamera.autoFocus(foucsCallback);
            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
    Camera.AutoFocusCallback foucsCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mCamera.cancelAutoFocus();
            if(success){
                Log.d(TAG, "onAutoFocus() is success2");
                mCamera.takePicture(null, null, tackpicturecallback);
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        myActivity.showWaitDialog();
                    }
                });
            }else{
                Log.d(TAG, "onAutoFocus() is failures");
//                mCamera.autoFocus(null);
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        myActivity.startTask();
                    }
                });
            }
        }
    };


    List<Camera.Size> PictureSizelist;
    List<Camera.Size> PreviewSizelist;
    Camera.Size PictureSize;
    Camera.Size PreviewSize;
//    int myWidth;
//    int myHeight;
    /**
     * 根据自己的大小来选择相同比例的最高的分辨率
     * @return
     */
    public void initCameraSizeConfig(float rate)
    {
//        myWidth = this.getWidth();
//        myHeight = this.getHeight();
//        Log.i(TAG,"--------display-----");
//        Log.i(TAG,myWidth+ "," + myHeight);
//        // 获取相机所支持的所有的尺寸

        //1.根据固定比例，寻找支持的所有 PictureSize和PreviewSize
        this.PictureSizelist = mCamera.getParameters().getSupportedPictureSizes();
        this.PreviewSizelist = mCamera.getParameters().getSupportedPreviewSizes();
        Log.i(TAG,"-----getSupportedPictureSizes----------");
        for (Camera.Size size : PictureSizelist){
            Log.i(TAG,size.width + "," +size.height);
        }
        Log.i(TAG,"-----getSupportedPreviewSizes----------");
        for (Camera.Size size : PreviewSizelist){
            Log.i(TAG,size.width + "," +size.height);
        }
        //1.1 是否需要对SizeList排序？


        //2.根据固定比例，寻找最大的PictureSize和PreviewSize
        this.PictureSize =  UIExtensin.getMaxSizeByRate(rate,this.PictureSizelist);
        this.PreviewSize =  UIExtensin.getMaxSizeByRate(rate,this.PreviewSizelist);

        Log.i(TAG,"-----PictureSizes----------");
        Log.i(TAG,this.PictureSize.width+ "," + this.PictureSize.height);
        Log.i(TAG,"-----PreviewSize----------");
        Log.i(TAG,this.PreviewSize.width+ "," + this.PreviewSize.height);

        //3.在设置摄像头的pictureSize和PreviewSize之后，重新设置浏览视图的大小
        myActivity.resizeView(this.PictureSize,this.PreviewSize);

        //4.将找到的size设置到摄像头上
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        mParameters.setPictureSize(this.PictureSize.width,this.PictureSize.height);
        mParameters.setPreviewSize(this.PreviewSize.width,this.PreviewSize.height);
        setDispaly(mParameters, mCamera);

        mCamera.setParameters(mParameters);
    }


}