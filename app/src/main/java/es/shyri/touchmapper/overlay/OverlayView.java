package es.shyri.touchmapper.overlay;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

abstract class OverlayView  implements View.OnTouchListener {
    private static final String TAG = OverlayService.class.getSimpleName();

    final Context context;
    final WindowManager windowManager;
    final DisplayMetrics metrics;
    protected WindowManager.LayoutParams params;

    View floatyView;

    public OverlayView(Context context, WindowManager windowManager) {
        this.context = context;
        this.windowManager = windowManager;
        this.params = getDefaultLayoutParams();

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

    public abstract WindowManager.LayoutParams getDefaultLayoutParams();

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

    protected static int getOverlayType() {
        int overlayType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        return overlayType;
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

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }
}
