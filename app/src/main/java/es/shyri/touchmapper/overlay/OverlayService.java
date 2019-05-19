package es.shyri.touchmapper.overlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import es.shyri.touchmapper.R;

public class OverlayService extends Service implements OnTouchListener {
    private static final String TAG = OverlayService.class.getSimpleName();

    private WindowManager windowManager;

    private View floatyView;

    private LayoutParams params;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        addOverlayView();
    }

    private void addOverlayView() {
        int overlayType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            overlayType = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            overlayType = LayoutParams.TYPE_SYSTEM_ALERT;

        }
        params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        overlayType,
                        LayoutParams.FLAG_KEEP_SCREEN_ON
                                | LayoutParams.FLAG_NOT_FOCUSABLE
                                | LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | LayoutParams.FLAG_HARDWARE_ACCELERATED
                                | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                                | LayoutParams.FLAG_SECURE,

                        PixelFormat.TRANSPARENT);

        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

        FrameLayout interceptorLayout = new FrameLayout(this) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                boolean handled = false;
                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    int keyCode = event.getKeyCode();
                    InputDevice mInputDevice = event.getDevice();


                    if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
                        if (event.getRepeatCount() == 0 && event.getKeyCode() != KeyEvent.KEYCODE_DPAD_CENTER &&
                                event.getKeyCode() != KeyEvent.KEYCODE_DEL && event.getKeyCode() != KeyEvent.KEYCODE_SPACE &&
                                event.getKeyCode() != KeyEvent.KEYCODE_SPACE) {
                            Log.v(TAG, mInputDevice.getName());
                            Log.v(TAG, mInputDevice.getDescriptor());
                            Log.v(TAG, getString(R.string.pressed_key, event.getKeyCode()));
                            handled = true;
                        }
                    }
                }

                return handled;
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                boolean handled = false;

                if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        final int historySize = event.getHistorySize();
                        for (int i = 0; i < historySize; i++) {
                            // Process the event at historical position i
                            processJoystickInput(event, i);
                        }

                        processJoystickInput(event, -1);

                        handled = true;
                    }
                }
                return handled;
            }
        };

        floatyView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.floating_view, interceptorLayout);
        floatyView.setOnTouchListener(this);
        windowManager.addView(floatyView, params);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (floatyView != null) {

            windowManager.removeView(floatyView);

            floatyView = null;
        }
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {
        InputDevice mInputDevice = event.getDevice();
        Log.v(TAG, mInputDevice.getDescriptor());
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float axis_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
        Log.v(TAG, getString(R.string.axis_x, axis_x));
        float axis_hat_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        Log.v(TAG, getString(R.string.axis_hat_x, axis_hat_x));
        float axis_z = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);
        Log.v(TAG, getString(R.string.axis_z, axis_z));

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float axis_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        Log.v(TAG, getString(R.string.axis_y, axis_y));
        float axis_hat_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        Log.v(TAG, getString(R.string.axis_hat_y, axis_hat_y));
        float axis_rz = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);
        Log.v(TAG, getString(R.string.axis_rz, axis_rz));

        float axis_rtrigger = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RTRIGGER, historyPos);
        Log.v(TAG, getString(R.string.axis_rtrigger, axis_rtrigger));
        float axis_ltrigger = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_LTRIGGER, historyPos);
        Log.v(TAG, getString(R.string.axis_ltrigger, axis_ltrigger));
        float axis_throttle = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_THROTTLE, historyPos);
        Log.v(TAG, getString(R.string.axis_throttle, axis_throttle));
        float axis_brake = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_BRAKE, historyPos);
        Log.v(TAG, getString(R.string.axis_brake, axis_brake));
        float axis_gas = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_GAS, historyPos);
        Log.v(TAG, getString(R.string.axis_gas, axis_gas));

    }

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value = historyPos < 0 ? event.getAxisValue(axis) : event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.v(TAG, "Touched...");
        params.flags ^= LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.updateViewLayout(floatyView, params);
        return true;
    }
}
