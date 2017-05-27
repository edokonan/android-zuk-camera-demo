package com.zuk.camerasample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ndco.ncameralib.camera.NCameraReturnInfo;
import com.ndco.ncameralib.camerasample.Demo1CameraActivity;

import static android.R.attr.x;
import static android.R.attr.y;

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
        } else{//再起動
            Log.i(TAG, "Not null at onCreate");
        }
        initUI();
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
    }

    // startActivityForResult で起動させたアクティビティが
    // finish() により破棄されたときにコールされる
    // requestCode : startActivityForResult の第二引数で指定した値が渡される
    // resultCode : 起動先のActivity.setResult の第一引数が渡される
    // Intent data : 起動先Activityから送られてくる Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        switch (requestCode) {
            case Demo1CameraActivity.ncamera_requestCode:
                if (resultCode == RESULT_OK) {
                    NCameraReturnInfo returnInfo = (NCameraReturnInfo) bundle.getSerializable("returninfo");
                    Toast toast = Toast.makeText(MainActivity.this, returnInfo.info1, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER| Gravity.CENTER, x, y);
                    toast.show();
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
