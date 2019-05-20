package es.shyri.touchmapper.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import es.shyri.touchmapper.R;

abstract class OverlayView  implements View.OnTouchListener {
    private static final String TAG = OverlayService.class.getSimpleName();

    final Context context;
    final WindowManager windowManager;
    final DisplayMetrics metrics;
    final WindowManager.LayoutParams params;

    View floatyView;

    public OverlayView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        this.context = context;
        this.windowManager = windowManager;
        this.params = params;

        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
    }

    abstract protected int getFloatyViewID();

    protected View getFloatyView() {
        if (floatyView == null) {
            FrameLayout layout = createLayout();

            floatyView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getFloatyViewID(), layout);
            floatyView.setOnTouchListener(this);
        }

        return floatyView;
    }

    public void addFloatyView() {
        windowManager.addView(getFloatyView(), params);
    }

    public void removeFloatyView() {
        if (floatyView != null) {
            windowManager.removeView(floatyView);
            floatyView = null;
        }
    }

    protected FrameLayout createLayout() {
        return new FrameLayout(context);
    }

    public static WindowManager.LayoutParams getDefaultLayoutParams() {
        WindowManager.LayoutParams params;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                            | LayoutParams.FLAG_NOT_FOCUSABLE
//                            | LayoutParams.FLAG_ALT_FOCUSABLE_IM
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_SECURE,
                    PixelFormat.TRANSLUCENT);

        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                            | LayoutParams.FLAG_NOT_FOCUSABLE
//                            | LayoutParams.FLAG_ALT_FOCUSABLE_IM
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_SECURE,
                    PixelFormat.TRANSLUCENT);
        }

        return params;
    }

    protected float px, py;

    protected void startMovingOverlay(float x, float y) {
        px = x;
        py = y;
    }

    protected void moveOverlay(float newX, float newY) {
        params.x += newX - px;
        params.y += newY - py;
        windowManager.updateViewLayout(floatyView, params);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.v(TAG, "Touched...");

        Log.v(TAG, "Motion Source: " + event.getSource() + " Evt: " + event.toString());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startMovingOverlay(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            moveOverlay(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}
