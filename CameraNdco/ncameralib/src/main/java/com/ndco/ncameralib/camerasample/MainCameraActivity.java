package com.ndco.ncameralib.camerasample;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ndco.ncameralib.camera.UIExtensin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.ndco.ncameralib.R;

public class MainCameraActivity extends AppCompatActivity implements Camera.PictureCallback, View.OnClickListener {

    private static final String TAG = MainCameraActivity.class.getSimpleName();
    private SurfacePreview mCameraSurPreview;
    private ImageView mCaptureButton;


    int screenWidth = 0;
    int screenHeight = 0;
    float screenRate ;
//    Size screenSize ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_main);

        // Create our Preview view and set it as the content of our activity.
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
//        mCameraSurPreview = (SurfacePreview) (SurfaceView) findViewById(R.id.surfaceView);
        mCameraSurPreview = new SurfacePreview(this);
        mCameraSurPreview.myActivity = this;
        preview.addView(mCameraSurPreview);
        initCameraPreviewViewSize();//

        // Add a listener to the Capture button
        mCaptureButton = (ImageView) findViewById(R.id.capture);
        mCaptureButton.setOnClickListener(this);
    }





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
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        // Restart the preview and re-enable the shutter button so that we can take another picture
        camera.startPreview();

        //See if need to enable or not
        mCaptureButton.setEnabled(true);
        Toast.makeText(this, "拍照成功", Toast.LENGTH_LONG).show();



    }




    @Override
    public void onClick(View v) {
        mCaptureButton.setEnabled(false);
        // get an image from the camera
        mCameraSurPreview.takePicture(this);
    }

    private File getOutputMediaFile(){
        //get the mobile Pictures directory
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //get the current time
        String timeStamp = new SimpleDateFormat("yyyy-MMdd-HH-mm-ss-SSS").format(new Date());
        return new File(picDir.getPath() + File.separator + "hejunlin_"+ timeStamp + ".jpg");
    }

    //设置camera的显示preview的size
    private void initCameraPreviewViewSize() {
        getScreenSize();

        ViewGroup.LayoutParams params = mCameraSurPreview.getLayoutParams();
        params.width = this.screenWidth;
        params.height = this.screenHeight;
        mCameraSurPreview.setLayoutParams(params);
    }


    //获取当前的屏幕size
    private void getScreenSize() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
//        this.screenSize = new Size(1,1);
        this.screenWidth = size.x;
        this.screenHeight = size.y;
        Log.i(TAG, "--------windows-----");
        Log.i(TAG, this.screenWidth + "," + this.screenHeight);
        this.screenRate = UIExtensin.getRate(this.screenWidth, this.screenHeight);
        Log.i(TAG, this.screenRate + ",");
    }
}
