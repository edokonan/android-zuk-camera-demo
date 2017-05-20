# README #

android camera demo

### Demo1 Demo1CameraActivity ###

* 调用摄像头类
* 根据摄像头的设置，修改浏览视图的大小
* 添加识别框
* 定时循环拍照
* 保存照片

### 配置参数类 Demo1CameraConfig ###
设置配置参数

* 照片的横竖比例 PicturesizeRate
    1.77 （16：9）
    1.33 （4：3）
* 识别框在preview中的位置
    overlay_rect_width_rate = 识别框宽/浏览视图的宽
    overlay_rect_height_rate = 识别框高/浏览视图的高

### 摄像头类 Demo1SurfacePreview ###

* 设置摄像头的PictureSize和PreviewSize 函数initCameraSizeConfig
    //1.根据固定比例，寻找支持的所有 PictureSize和PreviewSize
        this.PictureSizelist = mCamera.getParameters().getSupportedPictureSizes();
        this.PreviewSizelist = mCamera.getParameters().getSupportedPreviewSizes();
        
    //2.根据固定比例，寻找最大的PictureSize和PreviewSize
        this.PictureSize =  UIExtensin.getMaxSizeByRate(rate,this.PictureSizelist);
        this.PreviewSize =  UIExtensin.getMaxSizeByRate(rate,this.PreviewSizelist);
    //3.在设置摄像头的pictureSize和PreviewSize之后，重新设置浏览视图的大小
        myActivity.resizeView(this.PictureSize,this.PreviewSize);

    //4.将找到的size设置到摄像头
        mCamera.setParameters(mParameters);




