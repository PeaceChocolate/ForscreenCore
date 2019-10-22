package com.baidu.eduai.sdk.touping.screencapturefeature;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.baidu.eduai.sdk.touping.utils.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by tianhouchao on 2019/6/3.
 */

public class ScreenCaptureModel implements IConnectListener {

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private final ScreenCapPermissionGrant screenCapPermissionGrant;
    private ImageReader imageReader;
    private int mWidth = 1280, mHeight = 720;
    private long lastSendTime;
    private Handler mHandler;
    private int mScreenDensity;
    private OnPostScreenCaptureResult mPostScreenCaptureResult;
    private Bitmap mLastBitmap;
    private int pixelStride;
    private int rowStride;
    private int rowPadding;
    private Image.Plane[] planes;
    private ByteBuffer buffer;

    public ScreenCaptureModel(Context context, OnPostScreenCaptureResult postScreenCaptureResult) {
        screenCapPermissionGrant = new ScreenCapPermissionGrant(context);
        mScreenDensity = (int) context.getResources().getDisplayMetrics().density;
        mHandler = new Handler();
        this.mPostScreenCaptureResult = postScreenCaptureResult;
        setUpMediaProjection();
    }

    public void startScreenCapture() {
        if (mMediaProjection != null) {
            Log.e("socket", "------->>开始截屏");
            setUpVirtualDisplay();
        }
    }

    public void stopScreenCapture() {
        Log.e("socket", "------->>停止截屏");
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void setUpMediaProjection() {
        mMediaProjection = screenCapPermissionGrant.getMediaProjection();
    }

    private void setUpVirtualDisplay() {
        initImageReader();
        mMediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        }, mHandler);

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                mWidth, mHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        super.onPaused();
                    }

                    @Override
                    public void onResumed() {
                        super.onResumed();
                    }

                    @Override
                    public void onStopped() {
                        super.onStopped();
                    }
                }, mHandler);
    }


    private void initImageReader() {
        imageReader = ImageReader.newInstance(mWidth, mHeight, 0x1, 2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        handleImage(image);
                    }
                } catch (Exception e) {
                    Logger.e("imageReader 报错 ：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, mHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = null;
            ByteArrayInputStream bais = null;
            try {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

                bais = new ByteArrayInputStream(baos.toByteArray());

                long currentTime = System.currentTimeMillis();
//                Logger.e("截屏时间间隔：" + (currentTime - lastSendTime));
                if (currentTime - lastSendTime >= 50) {
                    if (mPostScreenCaptureResult != null) {
                        mPostScreenCaptureResult.onPostResult(bais);
                    }
                    lastSendTime = currentTime;
                }
            } catch (Exception e) {
                Logger.e("imageReader 报错 ：" + e.getMessage());
            } finally {
                if (baos != null) {
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (bais != null) {
                    try {
                        bais.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                if (bitmap != null) {
//                    bitmap.recycle();
//                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImage(Image image) {
        Bitmap bitmap = null;
        try {
            planes = image.getPlanes();
            buffer = planes[0].getBuffer();
            pixelStride = planes[0].getPixelStride();
            rowStride = planes[0].getRowStride();
            rowPadding = rowStride - pixelStride * mWidth;

            bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride,
                    mHeight, Bitmap.Config.ARGB_4444);
            bitmap.copyPixelsFromBuffer(buffer);
            handleBitmap(bitmap);
            mLastBitmap = bitmap;
        } catch (Exception e) {
            Logger.e("imageReader 报错 ：" + e.getMessage());
        } finally {
            if (image != null) {
                image.close();
            }
        }

    }


    @Override
    public void onConnectedSuccess() {
        Logger.e("connectedSuccess");
        if (mLastBitmap != null) {
            handleBitmap(mLastBitmap);
        }
    }

    @Override
    public void onConnectedError() {

    }

    @Override
    public void onServiceClose() {

    }

    public interface OnPostScreenCaptureResult {
        void onPostResult(InputStream inputStream);
    }


}
