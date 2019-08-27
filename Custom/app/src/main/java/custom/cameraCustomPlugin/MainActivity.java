package custom.cameraCustomPlugin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.cordova.CordovaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private CheckPermission checkPermission;// 检测权限类的权限检测器
    private Button btn;
    public static boolean isrequestCheck=true;// 判断是否需要系统权限检测。防止和系统提示框重叠
    private static final int PERMISSION_REQUEST_CODE = 0;// 系统授权管理页面时的结果参数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission = new CheckPermission(this);
        btn=findViewById(R.id.takePic);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("kkkkk", "onClick: ----");
                if (Build.VERSION.SDK_INT >= 23) {
                    CheckPermission checkPermission = new CheckPermission(MainActivity.this);
                    if (checkPermission.permissionSet(PERMISSION)) {
                        checkPermission();
                        //PermissionActivity.startActivityForResult(activity,0,"CameraActivity",  PERMISSION);
                    } else {
                        Intent cameraintent = new Intent(MainActivity.this,CameraActivity.class);
                        cameraintent.putExtra("type",1);
                        MainActivity.this.startActivityForResult(cameraintent,1);
                    }
                }else {
                    Intent cameraintent = new Intent(MainActivity.this,CameraActivity.class);
                    cameraintent.putExtra("type",1);
                    MainActivity.this.startActivityForResult(cameraintent,1);
                }
            }
        });
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
        Intent cameraintent = new Intent(this,CameraActivity.class);
        cameraintent.putExtra("type",1);
        this.startActivityForResult(cameraintent,1);

    }

    // 请求权限去兼容版本
    private void requestPermissions(String... permission) {
        if (Build.VERSION.SDK_INT >= 23) {

            this.requestPermissions(permission,PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode
                && hasAllPermissionGranted(grantResults)) // 判断请求码与请求结果是否一致
        {
            isrequestCheck = true;// 需要检测权限，直接进入，否则提示对话框进行设置
            allPermissionGranted(); // 进入

        } else { // 提示对话框设置
            isrequestCheck = false;
            // showMissingPermissionDialog();//dialog
            Toast.makeText(this, "您禁止了此权限！请选择允许", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.i("kkkkk", "onActivityResult: ---"+intent.getIntExtra("manualInput",2));
                    byte[] data = intent.getByteArrayExtra("data");
                    if(data!=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Log.i("kkk", "onActivityResult: -----"+bitmapToBase64(bitmap));
                    }

                }
                break;
            default:
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


    static final String[] PERMISSION = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
            Manifest.permission.CAMERA,Manifest.permission.VIBRATE, Manifest.permission.INTERNET};
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
}
