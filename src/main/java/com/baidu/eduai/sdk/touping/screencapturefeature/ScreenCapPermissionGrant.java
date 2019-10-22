package com.baidu.eduai.sdk.touping.screencapturefeature;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by huhuajun on 2019/5/29.
 */

public class ScreenCapPermissionGrant {
    private static final String TAG = "ScreenCapPermissionGrant";

    private static final String IMediaProjection = "android.media.projection.IMediaProjection";
    private static final String IMediaProjectionManager = "android.media.projection.IMediaProjectionManager";

    private Context mContext;
    private MediaProjectionManager mMediaProjectionManager;

    // server 端IMediaProjectionManager服务对象
    Object mIMediaProjectionManagerObj;
    // server 端IMediaProjection服务对象
    Object mIMediaProjectionObj;
    // server 端IMediaProjection Binder对象
    Object mIMediaProjectionBinderObj;

    int mUid;
    String mPackageName = "com.baidu.launcher";

    public ScreenCapPermissionGrant(Context context) {
        mContext = context;
        mMediaProjectionManager = (MediaProjectionManager) mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public MediaProjection getMediaProjection() {

        PackageManager packageManager = mContext.getPackageManager();
        ApplicationInfo aInfo;
        try {
            aInfo = packageManager.getApplicationInfo(mPackageName, 0);
            mUid = aInfo.uid;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Field mServiceField = mMediaProjectionManager.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);
            mIMediaProjectionManagerObj = mServiceField.get(mMediaProjectionManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method hasProjectionPermissionMethod = mIMediaProjectionManagerObj.getClass().getDeclaredMethod("hasProjectionPermission", int.class, String.class);
            hasProjectionPermissionMethod.setAccessible(true);
            Object resultObj = hasProjectionPermissionMethod.invoke(mIMediaProjectionManagerObj, mUid, mPackageName);
            Log.e("xxx", "---->>>>hasProjectionPermission result:" + resultObj);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("xxx", "---->>>>hasProjectionPermission err:" + e.getMessage());
        }

        try {
            Method createProjectionMethod = mIMediaProjectionManagerObj.getClass().getDeclaredMethod("createProjection", int.class, String.class, int.class, boolean.class);
            createProjectionMethod.setAccessible(true);
            mIMediaProjectionObj = createProjectionMethod.invoke(mIMediaProjectionManagerObj, mUid, mPackageName, 0, true);
            Log.e("xxx", "---->>>>createProjection result:" + mIMediaProjectionObj);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("xxx", "---->>>>createProjection err:" + e.getMessage());
        }

        try {
            Method asBinderMethod = mIMediaProjectionObj.getClass().getDeclaredMethod("asBinder");
            asBinderMethod.setAccessible(true);
            mIMediaProjectionBinderObj = asBinderMethod.invoke(mIMediaProjectionObj);
            Log.e("xxx", "---->>>>asBinder result:" + mIMediaProjectionBinderObj);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("xxx", "---->>>>asBinder err:" + e.getMessage());
        }

        if (mIMediaProjectionBinderObj != null) {
            try {
                Intent intent = new Intent();
                Method putExtraMethod = intent.getClass().getDeclaredMethod("putExtra", String.class, IBinder.class);
                putExtraMethod.setAccessible(true);
                putExtraMethod.invoke(intent, "android.media.projection.extra.EXTRA_MEDIA_PROJECTION", mIMediaProjectionBinderObj);
                Log.e("xxx", "---->>>>putExtra result:" + intent);
                MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent);
                return mediaProjection;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("xxx", "---->>>>putExtra err:" + e.getMessage());
            }
        }
        return null;
    }

}
