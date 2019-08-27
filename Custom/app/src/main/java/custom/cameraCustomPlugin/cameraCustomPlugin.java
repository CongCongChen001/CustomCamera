package custom.cameraCustomPlugin;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


/**
 * 项目名称：慧视OCR
 * 类名称：首界面
 * 类描述：
 * 创建人：黄震
 * 创建时间：2016/02/03
 * 修改人：${user}
 * 修改时间：${date} ${time}
 * 修改备注：
 */
public class cameraCustomPlugin extends CordovaPlugin {
    Activity activity;
    private CallbackContext callbackContext;
    private CheckPermission checkPermission;// 检测权限类的权限检测器
    public static boolean isrequestCheck=true;// 判断是否需要系统权限检测。防止和系统提示框重叠
    public static final int PERMISSION_GRANTED = 0;// 标识权限授权
    public static final int PERMISSION_DENIEG = 1;// 权限不足，权限被拒绝的时候
    private static final int PERMISSION_REQUEST_CODE = 0;// 系统授权管理页面时的结果参数
    private static final String EXTRA_PERMISSION = "com.wintone.permissiondemo";// 权限参数
    private static final String PACKAGE_URL_SCHEME = "package:";// 权限方案
    private int type;
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
        checkPermission = new CheckPermission(activity);
    }

    @Override
    //参数action是用来判断执行哪个方法，args是json格式的参数，callbackContext响应返回结果。
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext=callbackContext;
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, this.callbackContext);
            return true;
        }else if(action.equals("cameraCustomVin")){
            Boolean scanningFlag = args.getBoolean(0);
            this.cameraCustomVin(scanningFlag);
            return true;
        }else if(action.equals("cameraCustomPlate")){
            Boolean scanningFlag = args.getBoolean(0);
            this.cameraCustomPlate(scanningFlag);
            return true;
        }
        return false;
    }

    //私有方法--调用的功能方法
    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            //成功回调
            callbackContext.success(message);
        } else {
            //失败回调
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    static final String[] PERMISSION = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
            Manifest.permission.CAMERA,Manifest.permission.VIBRATE, Manifest.permission.INTERNET};
    /*
     * VIN true 扫描 false 拍照
     * */
    public void cameraCustomVin(Boolean scanningFlag){
        Log.i("kkk", "cameraCustomVin: "+scanningFlag);
        if(!scanningFlag){
            type=1;
            getCamera(1);
        }else {
            //扫描
        }
    }
    /*
     * 车牌号 true 扫描 false 拍照
     * */
    public void cameraCustomPlate(Boolean scanningFlag){
        if(!scanningFlag){
            type=2;
            getCamera(2);
        }else {
            //扫描
        }

    }

    private void getCamera(int value) {
        Intent cameraintent = new Intent(activity,CameraActivity.class);
        cameraintent.putExtra("type",value);
        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermission checkPermission = new CheckPermission(activity);
            if (checkPermission.permissionSet(PERMISSION)) {
                checkPermission();
                //PermissionActivity.startActivityForResult(activity,0,"CameraActivity",  PERMISSION);
            } else {
                this.cordova.startActivityForResult((CordovaPlugin) this,cameraintent,1);
                activity.overridePendingTransition(activity.getResources().getIdentifier("zoom_enter", "anim", activity.getApplication().getPackageName()), activity.getResources().getIdentifier("push_down_out", "anim", activity.getApplication().getPackageName()));
            }
        } else {
            this.cordova.startActivityForResult((CordovaPlugin) this,cameraintent,1);
            activity.overridePendingTransition(activity.getResources().getIdentifier("zoom_enter", "anim", activity.getApplication().getPackageName()), activity.getResources().getIdentifier("push_down_out", "anim", activity.getApplication().getPackageName()));
        }
    }
    private void checkPermission(){
        if (isrequestCheck) {
            if (checkPermission.permissionSet(PERMISSION)) {
                requestPermissions(PERMISSION); // 去请求权限
            } else {
//				System.out.println("第一次");
//				allPermissionGranted();// 获取全部权限
            }
        } else {
            isrequestCheck = true;
        }

    }
    // 获取全部权限
    private void allPermissionGranted() {
        Intent cameraintent = new Intent(activity,CameraActivity.class);
        cameraintent.putExtra("type",type);
        this.cordova.startActivityForResult((CordovaPlugin) this,cameraintent,1);
    }

    // 请求权限去兼容版本
    private void requestPermissions(String... permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            this.cordova.requestPermissions(this,PERMISSION_REQUEST_CODE,permission
            );
        }

    }
    /*
     * bitmap转base64
     * */
    private String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /*end*/


    public void onRequestPermissionResult(int requestCode,
                                          String[] permissions, int[] grantResults){

        if (PERMISSION_REQUEST_CODE == requestCode
                && hasAllPermissionGranted(grantResults)) // 判断请求码与请求结果是否一致
        {
            isrequestCheck = true;// 需要检测权限，直接进入，否则提示对话框进行设置
            allPermissionGranted(); // 进入

        } else { // 提示对话框设置
            isrequestCheck = false;
            // showMissingPermissionDialog();//dialog
            Toast.makeText(activity, "您禁止了此权限！请选择允许", Toast.LENGTH_SHORT).show();

        }

    }
    // 获取全部权限
    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i("kkk", "onActivityResult: ---"+requestCode);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //为2时进入手动输入页面
                    int manualInput = intent.getIntExtra("manualInput", 2);
                    byte[] data = intent.getByteArrayExtra("data");
                    if(data!=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        callbackContext.success(bitmapToBase64(bitmap));
                    }else {
                        callbackContext.success(manualInput);
                    }
                }
                break;
            default:
        }

    }


}
