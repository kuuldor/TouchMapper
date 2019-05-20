package es.shyri.touchmapper.overlay;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import es.shyri.touchmapper.R;

public class MapperOverlay extends OverlayView {
    private static final String TAG = OverlayService.class.getSimpleName();

    MapperOverlay(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context, windowManager, params);
    }

    @Override
    protected int getFloatyViewID() {
        return R.layout.mapper_view;
    }

    @Override
    protected FrameLayout createLayout() {
        return new FrameLayout(context) {
//            @Override
//            public boolean dispatchTouchEvent(MotionEvent event) {
//                Log.v(TAG, "Motion Source: " + event.getSource() + " Evt: " + event.toString());
//                return false;
//            }

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
                            int key = event.getKeyCode();
                            Log.v(TAG, mInputDevice.getName());
                            Log.v(TAG, mInputDevice.getDescriptor());
                            Log.v(TAG, context.getString(R.string.pressed_key, key));

                            if (key == KeyEvent.KEYCODE_BUTTON_THUMBL) {
                                letGoPaused();
                            }

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
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {
        InputDevice mInputDevice = event.getDevice();
        Log.v(TAG, mInputDevice.getDescriptor());
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float axis_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
        Log.v(TAG, context.getString(R.string.axis_x, axis_x));
        float axis_hat_x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
        Log.v(TAG, context.getString(R.string.axis_hat_x, axis_hat_x));
        float axis_z = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);
        Log.v(TAG, context.getString(R.string.axis_z, axis_z));

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float axis_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        Log.v(TAG, context.getString(R.string.axis_y, axis_y));
        float axis_hat_y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        Log.v(TAG, context.getString(R.string.axis_hat_y, axis_hat_y));
        float axis_rz = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);
        Log.v(TAG, context.getString(R.string.axis_rz, axis_rz));

        float axis_rtrigger = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RTRIGGER, historyPos);
        Log.v(TAG, context.getString(R.string.axis_rtrigger, axis_rtrigger));
        float axis_ltrigger = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_LTRIGGER, historyPos);
        Log.v(TAG, context.getString(R.string.axis_ltrigger, axis_ltrigger));
        float axis_throttle = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_THROTTLE, historyPos);
        Log.v(TAG, context.getString(R.string.axis_throttle, axis_throttle));
        float axis_brake = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_BRAKE, historyPos);
        Log.v(TAG, context.getString(R.string.axis_brake, axis_brake));
        float axis_gas = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_GAS, historyPos);
        Log.v(TAG, context.getString(R.string.axis_gas, axis_gas));

        Log.v(TAG, "Metrics: "+ metrics.widthPixels + "x" + metrics.heightPixels);
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

    private void letGoPaused() {
        toggleFocusable();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleFocusable();
            }
        }, 100);

    }

    private void toggleFocusable() {
        params.flags ^= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.updateViewLayout(floatyView, params);
    }

    protected void moveOverlay(float newX, float newY) {
        params.x += newX - px;
        windowManager.updateViewLayout(floatyView, params);
    }

}
