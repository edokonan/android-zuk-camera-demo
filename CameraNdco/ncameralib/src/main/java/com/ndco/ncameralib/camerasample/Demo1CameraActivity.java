package com.ndco.ncameralib.camerasample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ndco.ncameralib.camera.NCameraReturnInfo;
import com.ndco.ncameralib.camera.PictureExtensin;
import com.ndco.ncameralib.camera.UIExtensin;
import com.ndco.ncameralib.camera.overlayContent;
import com.ndco.ncameralib.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static java.lang.Thread.enumerate;
import static java.lang.Thread.sleep;

public class Demo1CameraActivity extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener {

    private static final String TAG = Demo1CameraActivity.class.getSimpleName();
    public static final int ncamera_requestCode = 123;


    RelativeLayout frameLayout_preview; //显示浏览图像的容器View
    private Demo1SurfacePreview mCameraSurPreview; //相机实时浏览View

    RelativeLayout overlay_view; //显示识别框框的容器View
    overlayContent rectview; //识别框

    TextView debug_infoView;
    TextView debug_camera_infoView;


///    Size screenSize ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_demo1_activity);

        // Create our Preview view and set it as the content of our activity.
        frameLayout_preview = (RelativeLayout) findViewById(R.id.camera_preview);
        overlay_view = (RelativeLayout) findViewById(R.id.overlay_view);
        debug_infoView = (TextView) findViewById(R.id.debug_infoView);
        debug_camera_infoView = (TextView) findViewById(R.id.debug_camera_infoView);

//        mCameraSurPreview = (SurfacePreview) (SurfaceView) findViewById(R.id.surfaceView);
        mCameraSurPreview = new Demo1SurfacePreview(this);
        mCameraSurPreview.myActivity = this;
        frameLayout_preview.addView(mCameraSurPreview);

        rectview = new overlayContent(this);
        overlay_view.addView(rectview);


    }



    AlertDialog waitDialog;
    public void showWaitDialog() {
        AlertDialog.Builder pWaitDailogBuilder = new AlertDialog.Builder(Demo1CameraActivity.this);
        pWaitDailogBuilder.setMessage("イメージ処理中..");
        waitDialog = pWaitDailogBuilder.show();
    }
    public void closeWaitDialog(){
        if(waitDialog!=null)
            waitDialog.dismiss();
    }
    String picturePath;
    // 保存照片
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //save the picture to sdcard
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file: " + e.getMessage());
        }
        // Restart the preview and re-enable the shutter button so that we can take another picture
        camera.startPreview();
        //See if need to enable or not
//        mCaptureButton.setEnabled(true);
        picturePath = pictureFile.getPath();
        debug_infoView.setText("拍照成功:" + picturePath + "count:" + icount);
        // TODO 解析
        boolean ret = parsepicture();
        NCameraReturnInfo returnInfo = new NCameraReturnInfo();
        closeWaitDialog();
        if (!ret){
            debug_infoView.setText("拍照成功:" + picturePath + " count:" + icount + " 识别失败，等待3秒" );
            startTask();
        }else{
//            int resultCode = ...;
            Intent resultIntent = new Intent();
            resultIntent.putExtra("returninfo", returnInfo);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    private File getOutputMediaFile(){
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyy-MMdd-HHmmss").format(new Date());
        return new File(picDir.getPath() + File.separator+ "ZCamera-" + timeStamp + ".jpg");
    }

    @Override
    public void onClick(View v) {
//        mCaptureButton.setEnabled(false);
        // get an image from the camera
        mCameraSurPreview.takePicture(this);
    }


//    //设置camera的显示preview的size
//    private void initCameraPreviewViewSize() {
//        getScreenSize();
//
//        ViewGroup.LayoutParams params = mCameraSurPreview.getLayoutParams();
//        params.width = this.screenWidth;
//        params.height = this.screenHeight;
//        mCameraSurPreview.setLayoutParams(params);
//    }

    //获取屏幕的size
    int screenWidth = 0;
    int screenHeight = 0;
    float screenRate ;
    //设置浏览图的size
    int previewWidth = 0;
    int previewHeight = 0;

    //3.在设置摄像头的pictureSize和PreviewSize之后，重新设置浏览视图的大小
    public void resizeView(Camera.Size Camera_PictureSize,Camera.Size Camera_PreviewSize) {
        //获取屏幕的size
        getScreenSize();

        //根据比例设置浏览视图的大小:
//        竖屏计算
//        previewWidth = screenWidth/2;
//        previewHeight = (int) ((float)previewWidth * ((float)Camera_PreviewSize.height / (float)Camera_PreviewSize.width));
//        横屏计算
        previewHeight = screenHeight;
        previewWidth = (int) ((float)previewHeight * ((float)Camera_PreviewSize.width / (float)Camera_PreviewSize.height));

        //设置识别框的位置
        rectview.setParam(
                this.screenWidth,this.screenHeight,
                previewWidth,previewHeight,
                Demo1CameraConfig.overlay_rect_width_rate,
                Demo1CameraConfig.overlay_rect_height_rate);

        //重新设置浏览视图容器的大小
        RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(previewWidth,previewHeight);
        layout_description.addRule(CENTER_IN_PARENT);
        frameLayout_preview.setLayoutParams(layout_description);

        //重新设置浏览视图的大小
        ViewGroup.LayoutParams params = mCameraSurPreview.getLayoutParams();
        params.width = previewWidth;
        params.height = previewHeight;
        mCameraSurPreview.setLayoutParams(params);

        String infostr = "Camera PictureSize:" + Camera_PictureSize.width+","+Camera_PictureSize.height
                    + " PreviewSize:"+ Camera_PreviewSize.width+","+Camera_PreviewSize.height
                + " ScreenSize:"+ this.screenWidth+","+this.screenHeight
                + " ViewSize:"+ this.previewWidth+","+this.previewHeight;
        debug_camera_infoView.setText(infostr);
        //start take picture
        startTask();
    }
    //获取当前的屏幕size
    private void getScreenSize() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screenWidth = size.x;
        this.screenHeight = size.y;
        Log.i(TAG, "--------windows-----");
        Log.i(TAG, this.screenWidth + "," + this.screenHeight);
        this.screenRate = UIExtensin.getRate(this.screenWidth, this.screenHeight);
        Log.i(TAG, this.screenRate + ",");
    }
    //定时拍照
    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
        // TODO 拍照
        tackpicture();
        }
    };


    void tackpicture(){
        this.showWaitDialog();
        if (mCameraSurPreview!=null){
            mCameraSurPreview.takePicture(this);
        }else{
            stopTask();
        }
    }
    int icount = 0;
    //解析
    PictureExtensin pictureExtensin = new PictureExtensin();
    boolean parsepicture(){
        try {
            pictureExtensin.setPicturePath(this.picturePath,
                    Demo1CameraConfig.overlay_rect_width_rate,
                    Demo1CameraConfig.overlay_rect_height_rate);

            Log.i(TAG, pictureExtensin.bitmap.toString());
            Log.i(TAG, pictureExtensin.top_left_point.toString());
            Log.i(TAG, pictureExtensin.top_right_point.toString());
            Log.i(TAG, pictureExtensin.buttom_left_point.toString());
            Log.i(TAG, pictureExtensin.buttom_right_point.toString());

            Thread.sleep(2000); //1000
        } catch (InterruptedException e) {

        }
        icount++;
        if (icount>5){
            return true;
        }
        return false;
    }

    // 开始拍照
    void startTask(){handler.postDelayed(runnable, 3000);}
    void stopTask(){
        handler.removeCallbacks(runnable);
    }
}
