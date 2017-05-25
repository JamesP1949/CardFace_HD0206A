package com.wis.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.common.cache.WeakMemoryCache;
import com.socks.library.KLog;
import com.wis.application.AppCore;
import com.wis.utils.GlobalConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by ybbz on 16/7/28.
 * 相机预览控制
 * 使用 {@link WeakMemoryCache} 保存oneShot图片的引用
 */

public class CameraPreview_ extends SurfaceView {
    private final static float DIGREE_90 = 90;
    private final static float DIGREE_180 = 180;
    private final static float DIGREE_270 = 270;
    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    public int cameraId;
    public static int mNumberOfCameras;
    private boolean isOpenCamera;
    public static boolean isCameraPreViewStarted; // 是否开始预览了
    private Bitmap mBitmap;
    private float aspectRatio; // 相机预览界面宽高比
    private int mWidth, mHeight;
    private Map.Entry<String, Reference<Bitmap>> mEntry;
    @Inject
    WeakMemoryCache mMemoryCache;

    @SuppressWarnings("deprecation")
    public CameraPreview_(Context context) {
        super(context);
        init(context);
    }

    public CameraPreview_(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        AppCore.getAppComponent().inject(this);
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(new CustomCallBack());
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mNumberOfCameras = Camera.getNumberOfCameras();
    }

    private class CustomCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (isOpenCamera) return;
            try {
                initCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            KLog.e("width--" + width + ", height--" + height);
            mWidth = width;
            mHeight = height;
            aspectRatio = (float) width / (float) height;
            if (mHolder.getSurface() == null) {
                return;
            }

            try {
                mCamera.stopPreview();
                isCameraPreViewStarted = false;
                Intent intent = new Intent(GlobalConstant.Action_Camera_Status);
                intent.putExtra(GlobalConstant.Action_Camera_Preview, true);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                setParameter();
                setDisplayOrientation();
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                KLog.e("曝光指数：" + mCamera.getParameters().getExposureCompensation());
                isCameraPreViewStarted = true;
                KLog.e("开始预览");
            } catch (Exception e) {
                e.printStackTrace();
                isCameraPreViewStarted = false;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera) {
                return;
            }
            KLog.e("预览界面被销毁了...");
            mHolder.removeCallback(this);
            freeCameraResource();
        }
    }

    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        mNumberOfCameras = Camera.getNumberOfCameras();
        // 默认打开后置相机
        try {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mCamera = Camera.open(cameraId);
            isOpenCamera = true;
            KLog.e("相机开启。。。");
        } catch (Exception e) {
            freeCameraResource();
            e.printStackTrace();
        }
    }

    /**
     * 解决捕获的画面方向和手机的方向不一致的问题
     */
    private void setDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);

        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;   // compensate the mirror
            KLog.e("orientation:" + info.orientation + ", degrees:" + degrees);
            KLog.e("前置反转角度：" + result);
        } else {
            // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        mCamera.setDisplayOrientation(result);
    }


    /**
     * 设置相机参数
     */
    private void setParameter() {

        Camera.Parameters mParameters = mCamera.getParameters();
        KLog.e("支持的面部识别数：" + mParameters.getMaxNumDetectedFaces());
        KLog.e("原始数据：" + mParameters.getPreviewSize().width + ", " + mParameters.getPreviewSize()
                .height);
        int minExposureCompensation = mParameters.getMinExposureCompensation();
        int maxExposureCompensation = mParameters.getMaxExposureCompensation();
        int compensation = mParameters.getExposureCompensation();
        // 设置曝光补偿指数
        if (compensation == 0) {
            if (maxExposureCompensation >= 2)
                mParameters.setExposureCompensation(2);
            else
                mParameters.setExposureCompensation(1);
        }
        KLog.e("曝光补偿指数：min:" + minExposureCompensation + ", max:" + maxExposureCompensation);
        if (aspectRatio != 0) {
            updatePreViewParameters(mParameters);
        }
        mParameters.setJpegQuality(100); // 图片质量
        KLog.e("Format" + mParameters.getPictureFormat());
        /*// 竖屏
        mParameters.set("orientation", "portrait");*/
        mCamera.setParameters(mParameters);
    }

    /**
     * 解决预览图像变形
     * 根据屏幕尺寸变化得到最合适的预览尺寸
     *
     * @param parameters
     */
    private void updatePreViewParameters(Camera.Parameters parameters) {
        int bestX = 0;
        int bestY = 0;
        float temp = 0f;
        float mid_diff = 100f;
        int newDiff = 0;
        int diff = Integer.MAX_VALUE;

        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        KLog.e("aspectRatio--" + aspectRatio);
        for (Camera.Size size : supportedPreviewSizes) {
            temp = Math.abs((float) size.width / (float) size.height - aspectRatio);
            if (temp <= mid_diff) { // 取宽高比差值最小的
                mid_diff = temp;
                newDiff = Math.abs(size.width - mWidth) + Math.abs(size.height - mHeight);
                if (newDiff < diff) { // 宽高比差值相等时取宽高差值最小的即最合适的
                    diff = newDiff;
                    bestX = size.width;
                    bestY = size.height;
                }
            }
        }
        KLog.e("bestX--" + bestX + ", bestY--" + bestY);
        parameters.setPreviewSize(bestX, bestY);
//        parameters.setPictureSize(bestX, bestY);
    }

    /**
     * 调整相机的曝光补偿指数
     * 一般曝光补偿指数为1就够用了
     */
    public void controlExposureCompensation() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters mParameters = mCamera.getParameters();
        int exposureCompensation = mParameters.getExposureCompensation();
        if (exposureCompensation == 0) {
            mCamera.stopPreview();
            mParameters.setExposureCompensation(2);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        } else {
            mCamera.stopPreview();
            mParameters.setExposureCompensation(0);
            mCamera.setParameters(mParameters);
            mCamera.startPreview();
        }
    }

    /**
     * 前后置相机切换
     */
    public void switchCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo); //关键
        // 后置变前置
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
            try {
                mCamera = Camera.open(cameraId);//打开当前选中的摄像头
                isOpenCamera = true;
                setParameter();
                setDisplayOrientation();
                mCamera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                mCamera.startPreview();//开始预览
            } catch (IOException e) {
                e.printStackTrace();
                isOpenCamera = false;
            }
        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mCamera.stopPreview();//停掉原来摄像头的预览
            mCamera.release();//释放资源
            mCamera = null;//取消原来摄像头
            try {
                mCamera = Camera.open(cameraId);//打开当前选中的摄像头
                isOpenCamera = true;
                setParameter();
                setDisplayOrientation();
                mCamera.setPreviewDisplay(mHolder);//通过surfaceview显示取景画面
                mCamera.startPreview();//开始预览
            } catch (IOException e) {
                e.printStackTrace();
                isOpenCamera = false;
            }
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public Map.Entry<String, Reference<Bitmap>> take() {
        if (mCamera == null) return null;
        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                decodeToBitMap(data, camera);
            }
        });

        return mEntry;
    }

    private void decodeToBitMap(byte[] data, Camera _camera) {
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        /**
         * 至关重要 对前后置切换拍照功能
         * 载入当前摄像头的信息 否则在switch(cameraInfo.facing)方法中
         * facing永远为后置情形 不会进入前置判断
         */
        Camera.getCameraInfo(cameraId, cameraInfo);
        Bitmap bmp = null;
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 60, stream);
//                KLog.e("Stream---" + stream.toByteArray().length);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size(),
                        options);
                options.inJustDecodeBounds = false;
                float outW = options.outWidth;
                float outH = options.outHeight;
//                KLog.e("outW:" + outW + ", outH:" + outH);
                float w = getWidth();
                float h = getHeight();
                int scaleSize = (int) Math.min(outW / w, outH / h);
                if (scaleSize <= 0) scaleSize = 1;
//                KLog.e("scaleSize---" + scaleSize);
                options.inSampleSize = scaleSize;
                options.inPreferredConfig = Bitmap.Config.RGB_565; // 解码方式选择最节省内存的565
                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size(),
                        options);
                stream.close();
                if (bmp == null)
                    KLog.e("没有形成图片");

                Matrix matrix = new Matrix();
                switch (cameraInfo.facing) {
                    case Camera.CameraInfo.CAMERA_FACING_FRONT://前
                        if (cameraInfo.orientation == 90) {
                            matrix.preRotate(DIGREE_270);  // 特殊设备本身是横屏 不需要旋转
                        }
                        /**
                         * 水平镜像反转 不然呈现的画面是左右反转的
                         */
                        matrix.postScale(-1, 1);
                        break;
                    case Camera.CameraInfo.CAMERA_FACING_BACK://后
                        matrix.preRotate(DIGREE_90);
                        break;
                }

                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix,
                        true);
                String key = String.valueOf(System.currentTimeMillis());
                mMemoryCache.put(key, bmp);

//                mBitmap = mMemoryCache.get(key);
                mEntry = mMemoryCache.getEntry(key);
//                KLog.e("View size---width" + getWidth() + ", height:" + getHeight());
//                KLog.e("bitmap width:" + bmp.getWidth() + ", height:" + bmp.getHeight());
            }
        } catch (Exception ex) {
            KLog.e("Sys", "Error:" + ex.getMessage());
            mEntry = null;
        }
    }

    /**
     * 释放相机资源
     */
    public void freeCameraResource() {
        if (mCamera != null) {
            KLog.e("释放相机资源");
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isOpenCamera = false;
            isCameraPreViewStarted = false;
        }
    }
}