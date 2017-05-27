package com.zuk.camerasample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btn_camera,btn_camera_demo1;

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
        }
        else{//再起動
            Log.i(TAG, "Not null at onCreate");
        }
        initUI();
    }

    void initUI(){
        btn_camera = (Button) findViewById(com.zuk.camerasample.R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MainCameraActivity.class);
//                myIntent.putExtra("activaty", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });


        btn_camera_demo1 = (Button) findViewById(com.zuk.camerasample.R.id.btn_camera_demo1);
        btn_camera_demo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Demo1CameraActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

    }

}
