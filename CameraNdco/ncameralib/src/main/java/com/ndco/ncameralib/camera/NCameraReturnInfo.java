package com.ndco.ncameralib.camera;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ksymac on 2017/05/27.
 */
public class NCameraReturnInfo implements Serializable {

    int result = 0;
    List<String> texts;
    String message;
    List<Bitmap> imgs;

    List<String> imgFileNames;

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setImgs(List<Bitmap> message) {
        this.imgs = imgs;
    }

    public List<Bitmap> getImgs() {
        return imgs;
    }


    public void setImgFileNames(List<String> imgFileNames) {
        this.imgFileNames = imgFileNames;
    }

    public List<String> getImgFileNames() {
        return imgFileNames;
    }


}