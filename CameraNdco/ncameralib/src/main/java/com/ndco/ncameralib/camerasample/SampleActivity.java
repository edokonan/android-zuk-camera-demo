package com.ndco.ncameralib.camerasample;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by eugene on 2017/05/29.
 */

public class SampleActivity
        extends AppCompatActivity
        implements Camera.PictureCallback, View.OnClickListener {

    private Demo1SurfacePreview mCameraSurPreview;

    public void onPictureTaken(byte[] data, Camera camera) {
        camera.startPreview();

        closeWaitDialog();
        if (!ret){
            //debug_infoView.setText("拍照成功:" + picturePath + " count:" + icount + " 识别失败，等待3秒" );
            startTask();
        }else{
//            int resultCode = ...;
            Intent resultIntent = new Intent();
            //resultIntent.putExtra("returninfo", returnInfo);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

    }


    @Override
    public void onClick(View v) {
        mCameraSurPreview.takePicture(this);
    }


    public void resizeView(Camera.Size Camera_PictureSize,Camera.Size Camera_PreviewSize) {
        startTask();
    }

    void startTask(){handler.postDelayed(runnable, 5000);}
    void stopTask(){
        handler.removeCallbacks(runnable);
    }


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

    AlertDialog waitDialog;
    public void showWaitDialog() {
        AlertDialog.Builder pWaitDailogBuilder = new AlertDialog.Builder(SampleActivity.this);
        pWaitDailogBuilder.setMessage("イメージ処理中..");
        waitDialog = pWaitDailogBuilder.show();
    }
    public void closeWaitDialog(){
        if(waitDialog!=null)
            waitDialog.dismiss();
    }

    boolean ret = parsepicture();
    boolean parsepicture() {
        //***
        return true;
    }



}
