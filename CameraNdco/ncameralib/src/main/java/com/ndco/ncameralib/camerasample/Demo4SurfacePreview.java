package com.ndco.ncameralib.camerasample;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ndco.ncameralib.camera.Demo1CameraConfig;
import com.ndco.ncameralib.camera.UIExtensin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by zhukui on 2017/5/30.
 */
public class Demo4SurfacePreview extends SurfaceView implements SurfaceHolder.Callback {
    public Demo1CameraActivity myActivity;
    private static final String TAG = Demo4SurfacePreview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    boolean foucus_again=false;

    public Demo4SurfacePreview(Context context) {
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
//            resetCamera();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }
    //初始化后，调用focus
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(TAG, "surfaceChanged() is called");
        try {
            mCamera.autoFocus(init_camera_autofocus_callback);
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    //初始化后，foucs成功后，设为continousfoucs
    Camera.AutoFocusCallback init_camera_autofocus_callback = new Camera.AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.w(TAG, "init onAutoFocus() is " + success);
            if(foucus_again) {
                if (success) {
                    camera.cancelAutoFocus();
                    set_camera_continuous_foucs();
                } else {
                    camera.autoFocus(init_camera_autofocus_callback);
                }
            }else{
                camera.cancelAutoFocus();
                set_camera_continuous_foucs();
            }
        }
    };
    //设为continousfoucs
    private void set_camera_continuous_foucs() {
        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        setDispaly(mParameters, mCamera);
        mCamera.setParameters(mParameters);
        try {
            mCamera.startPreview();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
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


    Camera.PictureCallback tackpicture_callback;
    Camera.AutoFocusCallback tackpicture_foucsCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.w(TAG, "1.TakePicture -- onAutoFocus() :" + success);
            Log.d(TAG, "1.TakePicture -- Current Mode :" + mCamera.getParameters().getFocusMode());
            if(foucus_again){
                if(success){
                    mCamera.cancelAutoFocus();
                    mCamera.takePicture(null, null, tackpicture_callback);
                }else{
                    Log.w(TAG, "1.TakePicture -- again");
                    mCamera.autoFocus(tackpicture_foucsCallback);
                }
            }else{
                mCamera.cancelAutoFocus();
                mCamera.takePicture(null, null, tackpicture_callback);
            }
        }
    };

    //拍照
    public void takePicture(Camera.PictureCallback imageCallback,int iMode) {
        Log.w(TAG, "1. TakePicture ");
        tackpicture_callback = imageCallback;
        startTakePicture(iMode);
    }
    //开始拍照
    public void startTakePicture(int iMode){
        Log.d(TAG, "1. TakePicture ---startTakePicture-----------");
        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
//        if(iMode == Demo1CameraConfig.TakePicture_FOCUS_MODE_MACRO){
//            if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_MACRO))
//            {
//                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
//            }else{
//                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//        }else if(iMode == Demo1CameraConfig.TakePicture_FOCUS_MODE_AUTO){
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        final Rect targetFocusRect = new Rect(-900,-200,900,200);
        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        focusList.add(focusArea);
        mParameters.setFocusAreas(focusList);
        mParameters.setMeteringAreas(focusList);
        mCamera.setParameters(mParameters);
        try {
            mCamera.startPreview();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
//        mCamera.cancelAutoFocus();
//        try{
//            sleep(1000);
//        }catch (Exception e){
//        }
        mCamera.autoFocus(tackpicture_foucsCallback);
        showCameraCurrentMode();
    }
    public void endTakePicture(){
        Log.d(TAG, "2.endTakePicture-----------");
//        reset_camera_auto();
//        reset_camera_continous_auto();
        set_camera_continuous_foucs();
    }
    private void reset_camera_auto() {
        showCameraCurrentMode();
        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(mParameters);
        try {
            mCamera.startPreview();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        mCamera.autoFocus( reset_camera_foucsCallback);
    }
    Camera.AutoFocusCallback reset_camera_foucsCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.w(TAG, "--reset_camera_auto onAutoFocus:" + success);
            if(foucus_again){
                if(success){
                    mCamera.cancelAutoFocus();
                    reset_camera_continous_auto();
                }else{
                    Log.w(TAG, "--reset_camera_auto foucs again ");
                    mCamera.autoFocus(reset_camera_foucsCallback);
                }
            }else{
                mCamera.cancelAutoFocus();
                reset_camera_continous_auto();
            }
        }
    };
    private void reset_camera_continous_auto() {
        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        mCamera.setParameters(mParameters);
        try {
            mCamera.startPreview();
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }
        mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
        showCameraCurrentMode();
    }
//    //设为continousfoucs
//    private void set_camera_continuous_foucs() {
//        mCamera.stopPreview();
//        mParameters = mCamera.getParameters();
//        mParameters.setPictureFormat(PixelFormat.JPEG);
//        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
//        {
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        }
//        setDispaly(mParameters, mCamera);
//        mCamera.setParameters(mParameters);
//        try {
//            mCamera.startPreview();
//        }catch (Exception e){
//            Log.e(TAG, e.toString());
//        }
//        mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
//    }

    private String showCameraCurrentMode(){
        mParameters = mCamera.getParameters();
        Log.d(TAG, "---Current Mode :" + mParameters.getFocusMode());
        return mParameters.getFocusMode();
    }
    private  void showCameraAllMode(){
        Log.d(TAG, "---all mMode :");
        mParameters = mCamera.getParameters();
        List<String> listMode = mParameters.getSupportedFocusModes();
        for (int i = 0; i < listMode.size(); i++){
            Log.w(TAG, listMode.get(i));
        }
    }

    List<Camera.Size> PictureSizelist;
    List<Camera.Size> PreviewSizelist;
    Camera.Size PictureSize;
    Camera.Size PreviewSize;
    /**
     * 根据自己的大小来选择相同比例的最高的分辨率
     * 初始化相机，设置模式等
     * @return
     */
    public void initCameraSizeConfig(float rate)
    {
        // 获取相机所支持的所有的尺寸
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

        mParameters.setPictureFormat(PixelFormat.JPEG);
        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        setDispaly(mParameters, mCamera);

        mCamera.setParameters(mParameters);
    }


}