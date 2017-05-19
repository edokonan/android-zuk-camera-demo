package com.zuk.camerasample;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zuk.camera.UIExtensin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hejunlin on 2016/10/5.
 */
public class Demo1SurfacePreview extends SurfaceView implements SurfaceHolder.Callback {
    public Demo1CameraActivity myActivity;
    private static final String TAG = Demo1SurfacePreview.class.getSimpleName();
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public Demo1SurfacePreview(Context context) {
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
                    if(success){
                        Log.d(TAG, "onAutoFocus() is success");
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

    public void takePicture(Camera.PictureCallback imageCallback) {
        if (mCamera != null){
            mCamera.takePicture(null, null, imageCallback);
        }
    }



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
        for (Camera.Size size : PictureSizelist)
        {
            Log.i(TAG,size.width + "," +size.height);
        }

        Log.i(TAG,"-----getSupportedPreviewSizes----------");
        this.PreviewSizelist = mCamera.getParameters().getSupportedPreviewSizes();
        for (Camera.Size size : PreviewSizelist)
        {
            Log.i(TAG,size.width + "," +size.height);
        }
        //课题1 是否需要对SizeList排序
//        this.PictureSize =  UIExtensin.getMaxSizeByRate(myWidth,myHeight,this.PictureSizelist);
//        this.PreviewSize =  UIExtensin.getMaxSizeByRate(myWidth,myHeight,this.PreviewSizelist);

        this.PictureSize =  UIExtensin.getMaxSizeByRate((float) 1.77,this.PictureSizelist);
        this.PreviewSize =  UIExtensin.getMaxSizeByRate((float) 1.77,this.PreviewSizelist);

        Log.i(TAG,"-----PictureSizes----------");
        Log.i(TAG,this.PictureSize.width+ "," + this.PictureSize.height);
        Log.i(TAG,"-----PreviewSize----------");
        Log.i(TAG,this.PreviewSize.width+ "," + this.PreviewSize.height);

        myActivity.resizeView(this.PreviewSize.width,this.PreviewSize.height);

//        return list.get(0);
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
    }



//    /**
//     * 获取支持的最高的分辨率
//     * @return
//     */
//    public Camera.Size getMaxOutputSize()
//    {
//        // 获取相机所支持的所有的尺寸
//
//        Log.i(TAG,"-----PictureSizes----------");
//        List<Camera.Size> list = mCamera.getParameters().getSupportedPictureSizes();
//        Camera.Size maxPictureSize =  list.get(0);
//        for (Camera.Size size : list)
//        {
//            Log.i(TAG,size.width + "," +size.height);
//            if (size.width > maxPictureSize.width){
//                maxPictureSize=size;
//            }
//        }
//        Log.i(TAG,maxPictureSize.width + "," +maxPictureSize.height);
//        Log.i(TAG,"-----PreviewSizes----------");
//        List<Camera.Size> list2 = mCamera.getParameters().getSupportedPreviewSizes();
//        Camera.Size maxPreviewSize =  list2.get(0);
//
//        for (Camera.Size size : list2)
//        {
//            Log.i(TAG,size.width + "," +size.height);
//            if (size.width > maxPreviewSize.width){
//                maxPreviewSize=size;
//            }
//        }
//        Log.i(TAG,maxPreviewSize.width + "," +maxPreviewSize.height);
//
//
//        int swidth = this.getWidth();
//        int sheight = this.getHeight();
//        Log.i(TAG,"--------display-----");
//        Log.i(TAG,swidth+ "," + sheight);
//
////        WindowManager windowManager = myActivity.getWindowManager();
////        Display display = windowManager.getDefaultDisplay();
////        Point size = new Point();
////        display.getSize(size);
////        int width = size.x;
////        int height = size.y;
////        Log.i(TAG,"--------windows-----");
////        Log.i(TAG,width+ "," + height);
//
//        return list.get(0);
//    }
//


}
