package custom.cameraCustomPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 383 on 2019/4/4.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";
   private OnTakePictureListener listener;
    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
        initView();
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open();//开启相机
            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
       //followScreenOrientation(getContext(), mCamera);
      //(Activity activity, int cameraId, android.hardware.Camera camera
      setCameraDisplayOrientation((Activity) mContext,0,mCamera);
        mCamera.startPreview();
      setAutoFocus();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success="+success);
        }
    }
    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width,picSize.height);
        this.setLayoutParams(new RelativeLayout.LayoutParams((int) (height*(h/w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
      Log.i("kkk", "setCameraParams: ----------77777");
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }
    public void takePicture(){
        //设置参数,并拍照
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        Log.i("kkkk", "takePicture: ----"+jpeg);
        mCamera.takePicture(null, null, jpeg);


    }
  public static void setCameraDisplayOrientation (Activity activity, int cameraId, Camera camera) {
    Log.i("kkk", "setCameraDisplayOrientation: --------3333");
    Camera.CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(cameraId, info);
    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
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
    } else {
      // back-facing
      result = (info.orientation - degrees + 360) % 360;
    }
    camera.setDisplayOrientation(result);

  }

  public static void followScreenOrientation(Context context, Camera camera){
    Log.i("kkk", "followScreenOrientation: ------44444");
    final int orientation = context.getResources().getConfiguration().orientation;
    if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
      camera.setDisplayOrientation(180);
    }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
      camera.setDisplayOrientation(90);
    }
  }

    public void setAutoFocus(){
      Log.i("kkk", "setAutoFocus: ----聚焦");
        mCamera.autoFocus(this);
    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     *            h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }
    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG,"shutter");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");

        }
    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {

            BufferedOutputStream bos = null;
            Bitmap bm = null;
            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                if(listener!=null){
                    listener.takePictureResult(data);
                }
                String PATH = Environment.getExternalStorageDirectory().toString()
                        + "/DCIM/Camera/";

                File dirs = new File(PATH);
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }
                String filePath = PATH+System.currentTimeMillis()+".jpg";//照片保存路径
                File file = new File(filePath);
                if (!file.exists()){
                    file.createNewFile();
                }
                //旋转图片
              Log.i("kkkkk", "onPictureTaken: --44444----"+getBitmapDegree(filePath));
                bm=rotateBitmapByDegree(bm,90);
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                mContext.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" +filePath )));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();//输出

                    bos.close();//关闭
                    bm.recycle();// 回收bitmap空间
                    mCamera.stopPreview();// 关闭预览
                    mCamera.startPreview();// 开启预览
                  setAutoFocus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };
  /**
   * 读取图片的旋转的角度
   *
   * @param path
   *            图片绝对路径
   * @return 图片的旋转角度
   */
  private int getBitmapDegree(String path) {
    Log.i("kkk", "getBitmapDegree: -----"+path);
    int degree = 0;
    try {
      // 从指定路径下读取图片，并获取其EXIF信息
      ExifInterface exifInterface = new ExifInterface(path);
      // 获取图片的旋转信息
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL);
      Log.i("kkk", "getBitmapDegree: -----"+orientation);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return degree;
  }
  /**
   * 将图片按照某个角度进行旋转
   *
   * @param bm
   *            需要旋转的图片
   * @param degree
   *            旋转角度
   * @return 旋转后的图片
   */
  public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
    Bitmap returnBm = null;

    // 根据旋转角度，生成旋转矩阵
    Matrix matrix = new Matrix();
    matrix.postRotate(degree);
    try {
      // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
      returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    } catch (OutOfMemoryError e) {
    }
    if (returnBm == null) {
      returnBm = bm;
    }
    if (bm != returnBm) {
      bm.recycle();
    }
    return returnBm;
  }
    /**
     * @Title: ${enclosing_method}
     * @Description: 打开闪光灯
     * @param camera
     *            相机对象
     * @return void 返回类型
     * @throws
     */
    public void openCameraFlash(Camera camera) {
        try {
            if (mCamera == null)
                mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> flashList = parameters.getSupportedFlashModes();
            if (flashList != null
                    && flashList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    /**
     * @Title: ${enclosing_method}
     * @Description: 关闭闪光灯
     * @param camera
     *            相机对象
     * @return void 返回类型
     * @throws
     */
    public void closedCameraFlash(Camera camera) {
        try {
            if (mCamera == null)
                mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            List<String> flashList = parameters.getSupportedFlashModes();
            if (flashList != null
                    && flashList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public interface OnTakePictureListener {
        void takePictureResult(byte[] data);
    }
    public void setOnTakePictureListener(OnTakePictureListener listener) {
        this.listener = listener;
    }
}
