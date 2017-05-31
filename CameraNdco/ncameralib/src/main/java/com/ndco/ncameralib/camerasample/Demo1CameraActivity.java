package com.ndco.ncameralib.camerasample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import static android.view.Gravity.CENTER_VERTICAL;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_TOP;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static java.lang.Thread.enumerate;
import static java.lang.Thread.sleep;

public class Demo1CameraActivity extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener {

    private static final String TAG = Demo1CameraActivity.class.getSimpleName();
    public static final int ncamera_requestCode = 123;


    RelativeLayout main_view;
    RelativeLayout frameLayout_preview; //显示浏览图像的容器View
    private Demo1SurfacePreview mCameraSurPreview; //相机实时浏览View
//    private Demo3SurfacePreview mCameraSurPreview; //相机实时浏览View

    RelativeLayout overlay_view; //显示识别框框的容器View
    overlayContent rectview; //识别框

    TextView debug_infoView;
    TextView debug_camera_infoView;

    NCameraReturnInfo returnInfo = new NCameraReturnInfo();

///    Size screenSize ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_demo1_activity);

        //If authorisation not granted for camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            //ask for authorisation
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);


        main_view = (RelativeLayout) findViewById(R.id.main_view);
        // Create our Preview view and set it as the content of our activity.
        frameLayout_preview = (RelativeLayout) findViewById(R.id.camera_preview);
        overlay_view = (RelativeLayout) findViewById(R.id.overlay_view);
        debug_infoView = (TextView) findViewById(R.id.debug_infoView);
        debug_camera_infoView = (TextView) findViewById(R.id.debug_camera_infoView);

        mCameraSurPreview = new Demo1SurfacePreview(this);

        mCameraSurPreview.myActivity = this;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        frameLayout_preview.addView(mCameraSurPreview,layoutParams);

        rectview = new overlayContent(this);
        overlay_view.addView(rectview,layoutParams);
    }





    AlertDialog waitDialog;
    public void showWaitDialog() {
        AlertDialog.Builder pWaitDailogBuilder = new AlertDialog.Builder(Demo1CameraActivity.this);
        pWaitDailogBuilder.setMessage("処理中..");
        waitDialog = pWaitDailogBuilder.show();
    }
    public void closeWaitDialog(){
        if(waitDialog!=null)
            waitDialog.dismiss();
    }
    String picturePath;

    public void savePicture(byte[] data){
        File pictureFile = getOutputMediaFile();
        try {
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        picturePath=pictureFile.getPath();
        debug_infoView.setText("拍照成功:" + pictureFile.getPath() + " count:" + icount);
    }


    // 保存照片
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
//        Handler mHandler = new Handler(Looper.getMainLooper());
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });

        showWaitDialog();
//        //save the picture to sdcard
        savePicture(data);
        // Restart the preview and re-enable the shutter button so that we can take another picture
        camera.startPreview();

        // TODO 解析
        long start = System.currentTimeMillis();
        Log.v(TAG, "#parsepicture# start");
        boolean ret = parsepicture(data);
        long end = System.currentTimeMillis();
        Log.v(TAG, "#parsepicture# end: (processing time = " + (end-start)/1000.0 + "s)");

        closeWaitDialog();
        ret = false;
        if (!ret){
            startTask();
        }else{
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
        previewHeight = screenHeight-100;
        previewWidth = (int) ((float)previewHeight * ((float)Camera_PreviewSize.width / (float)Camera_PreviewSize.height));

        //重新设置浏览视图容器的大小
        RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(previewWidth,previewHeight);
//        layout_description.addRule(CENTER_IN_PARENT);

        layout_description.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layout_description.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        frameLayout_preview.setLayoutParams(layout_description);
        frameLayout_preview.invalidate();
//        overlay_view.setLayoutParams(layout_description);

        //重新设置浏览视图的大小
        ViewGroup.LayoutParams params = mCameraSurPreview.getLayoutParams();
        params.width = previewWidth;
        params.height = previewHeight;
        mCameraSurPreview.setLayoutParams(params);

        //设置识别框的位置
//        rectview.setParam(
//                this.screenWidth,this.screenHeight,
//                previewWidth,previewHeight,
//                Demo1CameraConfig.overlay_rect_width_rate,
//                Demo1CameraConfig.overlay_rect_height_rate);
        rectview.setViewRect(
                previewWidth,previewHeight,
                Demo1CameraConfig.overlay_rect_width_rate,
                Demo1CameraConfig.overlay_rect_height_rate);
        rectview.invalidate();
//        this.screenWidth,this.screenHeight,

        String infostr = "Camera PictureSize:" + Camera_PictureSize.width+","+Camera_PictureSize.height
                + " PreviewSize:"+ Camera_PreviewSize.width+","+Camera_PreviewSize.height
                + " ScreenSize:"+ this.screenWidth+","+this.screenHeight
                + " ViewSize:"+ this.previewWidth+","+this.previewHeight
                + " 识别Rect:"+ rectview.rect;
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
        if (mCameraSurPreview!=null){
            mCameraSurPreview.takePicture(this);
        }else{
            stopTask();
        }
    }

    int icount = 0;
    //解析
    PictureExtensin pictureExtensin = new PictureExtensin();
    boolean parsepicture(byte[] data){
        try {

            pictureExtensin.setBitmapWihtCameraData(data,
                    Demo1CameraConfig.overlay_rect_width_rate,
                    Demo1CameraConfig.overlay_rect_height_rate );
//            Log.i(TAG, pictureExtensin.bitmap.toString());
//            Log.i(TAG, pictureExtensin.top_left_point.toString());
//            Log.i(TAG, pictureExtensin.top_right_point.toString());
//            Log.i(TAG, pictureExtensin.buttom_left_point.toString());
//            Log.i(TAG, pictureExtensin.buttom_right_point.toString());
//
//            int rowStart = pictureExtensin.top_left_point.y;
//            int rowEnd   = pictureExtensin.buttom_left_point.y;
//            int colStart = pictureExtensin.top_left_point.x;
//            int colEnd   = pictureExtensin.top_right_point.x;


            sleep(200);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        icount++;
        if (icount>30){
            return true;
        }
        return false;
    }

    // 开始拍照
    public void startTask(){
        Log.i(TAG, "startTask");
        handler.postDelayed(runnable, 5000);
    }
    void stopTask(){
        handler.removeCallbacks(runnable);
    }
}

