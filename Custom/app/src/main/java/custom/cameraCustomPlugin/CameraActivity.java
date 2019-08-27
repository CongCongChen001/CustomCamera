package custom.cameraCustomPlugin;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CameraActivity extends Activity implements View.OnClickListener,RectOnCamera.IAutoFocus,CameraSurfaceView.OnTakePictureListener{

    private CameraSurfaceView mCameraSurfaceView;
    private RectOnCamera mRectOnCamera;
    private ImageButton takePicBtn;
    private ImageView iv_camera_flash,putpalte,iv_camera_back;
    private boolean flashOpen=false;//默认关闭
    private boolean isClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        setContentView(R.layout.activity_camera);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        mRectOnCamera = (RectOnCamera) findViewById(R.id.rectOnCamera);
        takePicBtn= findViewById(R.id.takePic);
        iv_camera_flash=findViewById(R.id.iv_camera_flash);
        putpalte=findViewById(R.id.putpalte);
        iv_camera_back=findViewById(R.id.iv_camera_back);
        mRectOnCamera.setIAutoFocus(this);
        mCameraSurfaceView.setOnTakePictureListener(this);
        takePicBtn.setOnClickListener(this);
        iv_camera_flash.setOnClickListener(this);
        putpalte.setOnClickListener(this);
        iv_camera_back.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_camera_back:
                CameraActivity.this.finish();
                break;
            case R.id.takePic:
                mCameraSurfaceView.takePicture();
                break;
            case R.id.iv_camera_flash:
                //手电筒
                if(flashOpen){
                    flashOpen=false;
                    mCameraSurfaceView.closedCameraFlash(null);
                }else {
                    flashOpen=true;
                    mCameraSurfaceView.openCameraFlash(null);
                }
                break;
            case R.id.putpalte:
                //手动输入车牌号
                Intent intent = new Intent(CameraActivity.this, cameraCustomPlugin.class);
                intent.putExtra("manualInput",2);
                setResult(RESULT_OK, intent);
                CameraActivity.this.finish();
                break;
            default:
                break;
        }
    }


    @Override
    public void autoFocus() {
        mCameraSurfaceView.setAutoFocus();
    }

    @Override
    public void takePictureResult(byte[] data) {
        Log.i("kkkkkk", "takePictureResult: -handledPos---"+data);
        Intent intent = new Intent(CameraActivity.this, cameraCustomPlugin.class);
        intent.putExtra("data",data);
        setResult(RESULT_OK, intent);
        CameraActivity.this.finish();
    }
}
