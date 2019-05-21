package es.shyri.touchmapper.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;

import es.shyri.touchmapper.R;

public class PointerOverlay extends OverlayView {

    public PointerOverlay(Context context, WindowManager windowManager) {
        super(context, windowManager);
    }

    @Override
    protected int getFloatyViewID() {
        return R.layout.pointer_view;
    }

    @Override
    public WindowManager.LayoutParams getDefaultLayoutParams() {
        int overlayType = getOverlayType();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
//                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
//                        | WindowManager.LayoutParams.FLAG_SECURE
                ,
                PixelFormat.TRANSPARENT);

        return params;
    }
}
