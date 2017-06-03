package com.ndco.ncameralib.camerasample;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.ndco.ncameralib.camera.UIExtensin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejunlin on 2016/10/5.
 */
public class Surface2Preview extends SurfaceView implements SurfaceHolder.Callback {
    public MainCameraActivity myActivity;
    private static final String TAG = Surface2Preview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public Surface2Preview(Context context) {
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
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);


            initCameraSizeConfig();
            mCamera.startPreview();
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
                    Log.w(TAG, "--surfaceChanged onAutoFocus:" + success);
                    if(success){
                        camera.cancelAutoFocus();
                        initCamera();
                    }
                }
            });
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void initCamera() {
        Log.d(TAG, "--initCamera---");

        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
//        mParameters.setPictureSize(4608,2592);
//        mParameters.setPreviewSize(1920,1080);
//        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        setDispaly(mParameters, mCamera);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
        //获取支持的最大size
//        getMaxOutputSize();
    }
    private void setDispaly(Camera.Parameters parameters,Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8){
            setDisplayOrientation(camera,90);
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

    public void takePicture(Camera.PictureCallback imageCallback) {
//        Log.d(TAG, "----------takePicture-----------");
//        mCamera.takePicture(null, null, imageCallback);
        tackpicturecallback = imageCallback;
        startTakePicture();
    }
    //设置拍照，然后
    public void startTakePicture(){
        Log.d(TAG, "1. TakePicture---startTakePicture-----------");
        mCamera.stopPreview();
        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_MACRO))
        {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        }else{
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        final Rect targetFocusRect = new Rect(-900,-200,900,200);
        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        focusList.add(focusArea);
        mParameters.setFocusAreas(focusList);
        mParameters.setMeteringAreas(focusList);

        mCamera.setParameters(mParameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();
        mCamera.autoFocus(tackpicture_foucsCallback);
        showCameraCurrentMode();
    }
    public void endTakePicture(){
        Log.d(TAG, "2.endTakePicture-----------");
//        reset_camera_auto();
        reset_camera_continous_auto();
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
            if(success){
                mCamera.cancelAutoFocus();
                reset_camera_continous_auto();
            }else{

                Log.w(TAG, "--reset_camera_auto foucs again ");
                mCamera.autoFocus(reset_camera_foucsCallback);
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



    private void showCameraCurrentMode(){
        mParameters = mCamera.getParameters();
//        List<String> listMode = mParameters.getSupportedFocusModes();
//        for (int i = 0; i < listMode.size(); i++){
//            Log.w(TAG, listMode.get(i));
//        }
        Log.d(TAG, "----------current mode :" + mParameters.getFocusMode());
    }


    Camera.PictureCallback tackpicturecallback;
    Camera.AutoFocusCallback tackpicture_foucsCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.w(TAG, "1.TakePicture-- onAutoFocus() :" + success);
            if(success){
                mCamera.cancelAutoFocus();
                mCamera.takePicture(null, null, tackpicturecallback);
                endTakePicture();
            }else{
                Log.w(TAG, "1.TakePicture-- again");
                mCamera.autoFocus(tackpicture_foucsCallback);
            }
        }
    };




    List<Camera.Size> PictureSizelist;
    List<Camera.Size> PreviewSizelist;
    Camera.Size PictureSize;
    Camera.Size PreviewSize;
    int myWidth;
    int myHeight;

    /**
     * 根据自己的大小来选择相同比例的最高的分辨率
     * @return
     */
    public void initCameraSizeConfig()
    {
        myWidth = this.getWidth();
        myHeight = this.getHeight();
        Log.i(TAG,"--------display-----");
        Log.i(TAG,myWidth+ "," + myHeight);
        // 获取相机所支持的所有的尺寸
        Log.i(TAG,"-----getSupportedPictureSizes----------");
        this.PictureSizelist = mCamera.getParameters().getSupportedPictureSizes();
        this.PreviewSizelist = mCamera.getParameters().getSupportedPreviewSizes();

        //课题1 是否需要对SizeList排序
        float screenrate = UIExtensin.getRate(myWidth,myHeight);
        this.PictureSize =  UIExtensin.getMaxSizeByRate(screenrate,this.PictureSizelist);
        this.PreviewSize =  UIExtensin.getMaxSizeByRate(screenrate,this.PreviewSizelist);


        mParameters = mCamera.getParameters();
        mParameters.setPictureFormat(PixelFormat.JPEG);
        mParameters.setPictureSize(this.PictureSize.width,this.PictureSize.height);
        mParameters.setPreviewSize(this.PreviewSize.width,this.PreviewSize.height);
//        if (mParameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
//        {
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        }
        setDispaly(mParameters, mCamera);
        mCamera.setParameters(mParameters);

        Log.i(TAG,"-----initCameraSizeConfig----------");
        showCameraCurrentMode();
    }



    /**
     * 获取支持的最高的分辨率
     * @return
     */
    public Camera.Size getMaxOutputSize()
    {
        // 获取相机所支持的所有的尺寸

        Log.i(TAG,"-----PictureSizes----------");
        List<Camera.Size> list = mCamera.getParameters().getSupportedPictureSizes();
        Camera.Size maxPictureSize =  list.get(0);
        for (Camera.Size size : list)
        {
            Log.i(TAG,size.width + "," +size.height);
            if (size.width > maxPictureSize.width){
                maxPictureSize=size;
            }
        }
        Log.i(TAG,maxPictureSize.width + "," +maxPictureSize.height);
        Log.i(TAG,"-----PreviewSizes----------");
        List<Camera.Size> list2 = mCamera.getParameters().getSupportedPreviewSizes();
        Camera.Size maxPreviewSize =  list2.get(0);

        for (Camera.Size size : list2)
        {
            Log.i(TAG,size.width + "," +size.height);
            if (size.width > maxPreviewSize.width){
                maxPreviewSize=size;
            }
        }
        Log.i(TAG,maxPreviewSize.width + "," +maxPreviewSize.height);


        int swidth = this.getWidth();
        int sheight = this.getHeight();
        Log.i(TAG,"--------display-----");
        Log.i(TAG,swidth+ "," + sheight);

        WindowManager windowManager = myActivity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Log.i(TAG,"--------windows-----");
        Log.i(TAG,width+ "," + height);

        return list.get(0);
    }



}
