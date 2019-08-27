package custom.cameraCustomPlugin;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;



/**
 * Created by dyk on 2016/4/7.
 */
public class RectOnCamera extends View {
    private IAutoFocus mIAutoFocus;
    private static final String TAG = "CameraSurfaceView";
    private int mScreenWidth;
    private int mScreenHeight;
    private Paint mPaint;
    private RectF mRectF;
    // 圆
    private Point centerPoint;
    private int radio;
    private  int color_red = 0x00FF00 ;
    public RectOnCamera(Context context) {
        this(context, null);
    }

    public RectOnCamera(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectOnCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenMetrix(context);
        initView(context);
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setDither(true);// 防抖动
        mPaint.setColor(color_red);
        mPaint.setAlpha(100);
        //mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);// 空心
        int marginLeft = (int) (mScreenWidth*0.1);
        int marginTop = (int) (mScreenHeight * 0.5);
        Log.i("kkkkk", "initView: -----"+(mScreenHeight - marginTop));
        mRectF = new RectF(marginLeft, marginTop-90, mScreenWidth - marginLeft, mScreenHeight-marginTop+170);

        centerPoint = new Point(mScreenWidth/2, mScreenHeight/2);
        radio = (int) (mScreenWidth*0.1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //mPaint.setColor(Color.RED);
        mPaint.setColor(color_red);
        mPaint.setAlpha(100);
        canvas.drawRect(mRectF, mPaint);
        mPaint.setColor(Color.WHITE);
        Log.i(TAG, "onDraw");
        //canvas.drawCircle(centerPoint.x,centerPoint.y, radio,mPaint);// 外圆
        //canvas.drawCircle(centerPoint.x,centerPoint.y, radio - 20,mPaint); // 内圆
    }
    /** 聚焦的回调接口 */
    public interface  IAutoFocus{
        void autoFocus();
    }

    public void setIAutoFocus(IAutoFocus mIAutoFocus) {
        this.mIAutoFocus = mIAutoFocus;
    }

}
