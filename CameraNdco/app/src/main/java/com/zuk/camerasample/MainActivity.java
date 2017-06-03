package com.zuk.camerasample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ndco.ncameralib.camera.NCameraReturnInfo;
import com.ndco.ncameralib.camerasample.Demo1CameraActivity;
//import com.ndco.ocr.OcrCtrl;
//import com.ndco.ocr.OcrUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.R.attr.x;
import static android.R.attr.y;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btn_camera,btn_camera_demo1;
    private TextView text_info;
    ImageView imageView5,imageView6,imageView7,imageView8;
    private TextView textView5,textView6,textView7,textView8,textView9,textView10,textView11,textView12;
    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //隐藏标题栏
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        if(savedInstanceState==null){//初回起動
            Log.i(TAG, "null at onCreate");
        } else{//再起動
            Log.i(TAG, "Not null at onCreate");
        }
        initUI();
        requestPermission();
    }

    static int REQUEST_CODE_CAMERA_PERMISSION = 333;
    static int REQUEST_CODE_WRITE_PERMISSION = 334;
    private void requestPermission(){
        ArrayList<String> strs = new ArrayList<String>();
        // 権限を取得する
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=PackageManager.PERMISSION_GRANTED) {
            strs.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            strs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            strs.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(strs.size()>0){
            String[] permissions = new String[strs.size()];
            for(int cnt=0;cnt<strs.size();cnt++)
            {
                permissions[cnt] = strs.get(cnt);
            }
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void initUI(){
        btn_camera = (Button) findViewById(com.zuk.camerasample.R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, com.ndco.ncameralib.camerasample.MainCameraActivity.class);
//                myIntent.putExtra("activaty", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });

        btn_camera_demo1 = (Button) findViewById(com.zuk.camerasample.R.id.btn_camera_demo1);
        btn_camera_demo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Demo1CameraActivity.class);
//                MainActivity.this.startActivity(myIntent);
                try{

                    MainActivity.this.startActivityForResult(myIntent, Demo1CameraActivity.ncamera_requestCode);
                    String xxx = "";
                }catch (Exception e){


                }
            }
        });
        text_info = (TextView) findViewById(R.id.text_info);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView7 = (ImageView) findViewById(R.id.imageView7);
        imageView8 = (ImageView) findViewById(R.id.imageView8);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        textView7 = (TextView) findViewById(R.id.textView7);
        textView8 = (TextView) findViewById(R.id.textView8);
        textView9 = (TextView) findViewById(R.id.textView9);
        textView10 = (TextView) findViewById(R.id.textView10);
        textView11 = (TextView) findViewById(R.id.textView11);
        textView12 = (TextView) findViewById(R.id.textView12);
    }

    // startActivityForResult で起動させたアクティビティが
    // finish() により破棄されたときにコールされる
    // requestCode : startActivityForResult の第二引数で指定した値が渡される
    // resultCode : 起動先のActivity.setResult の第一引数が渡される
    // Intent data : 起動先Activityから送られてくる Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        Bundle bundle = data.getExtras();
        switch (requestCode) {
            case Demo1CameraActivity.ncamera_requestCode:
                if (resultCode == RESULT_OK) {
                    NCameraReturnInfo returnInfo = (NCameraReturnInfo) bundle.getSerializable("returninfo");
//                    Toast toast = Toast.makeText(MainActivity.this, returnInfo.info1, Toast.LENGTH_LONG);
                    String dispText = "";
                    if (returnInfo.getTexts() != null) {
                        for (int i = 0; i < returnInfo.getTexts().size(); i++) {
                            dispText += "[item" +(i+1)+"] = " + returnInfo.getTexts().get(i) + ", ";
                        }
                        int size = returnInfo.getTexts().size();
                        textView5.setText("[item1] = ");
                        textView6.setText("[item2] = ");
                        textView7.setText("[item3] = ");
                        textView8.setText("[item4] = ");
                        if (size >= 1) textView5.setText(textView5.getText() + returnInfo.getTexts().get(0));
                        if (size >= 2) textView6.setText(textView6.getText() + returnInfo.getTexts().get(1));
                        if (size >= 3) textView7.setText(textView7.getText() + returnInfo.getTexts().get(2));
                        if (size >= 4) textView8.setText(textView8.getText() + returnInfo.getTexts().get(3));
                    }
                    dispText += "[errcode] = " + returnInfo.getResult() + ", ";
                    dispText += "[errcmsg] = " + returnInfo.getMessage();

                    text_info.setText(dispText);

                    if (returnInfo.getImgs() != null) {
                        int size = returnInfo.getImgs().size();
                        if (size >= 1) imageView5.setImageBitmap(returnInfo.getImgs().get(0));
                        if (size >= 2) imageView6.setImageBitmap(returnInfo.getImgs().get(1));
                        if (size >= 3) imageView7.setImageBitmap(returnInfo.getImgs().get(2));
                        if (size >= 4) imageView8.setImageBitmap(returnInfo.getImgs().get(3));
                    }
                    if (returnInfo.getImgFileNames() != null) {
                        int size = returnInfo.getImgFileNames().size();
//                        returnInfo.getImgFileNames().
                        if (size >= 1) {
                            File srcFile = new File(returnInfo.getImgFileNames().get(0));
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(srcFile);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            imageView5.setImageBitmap(bitmap);
                        }
                        if (size >= 2) {
                            File srcFile = new File(returnInfo.getImgFileNames().get(1));
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(srcFile);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            imageView6.setImageBitmap(bitmap);
                        }
                        if (size >= 3) {
                            File srcFile = new File(returnInfo.getImgFileNames().get(2));
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(srcFile);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            imageView7.setImageBitmap(bitmap);
                        }
                        if (size >= 4) {
                            File srcFile = new File(returnInfo.getImgFileNames().get(3));
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(srcFile);
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            imageView8.setImageBitmap(bitmap);
                        }
                    }

                    textView9.setText("[errcode] = " + returnInfo.getResult());
                    textView10.setText("[errmsg] = " + returnInfo.getMessage());
                } else if (resultCode == RESULT_CANCELED) {
//                    text.setText(
//                            "requestCode:" + requestCode
//                                    + "\nresultCode:" + resultCode
//                                    + "\ndata:" + bundle.getString("key.canceledData"));
                }
                break;

            default:
                break;
        }
    }
}