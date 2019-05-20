package es.shyri.touchmapper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import es.shyri.touchmapper.overlay.OverlayService;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 62345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonOpenTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControllerTestActivity.openActivity(MainActivity.this);
            }
        });


        findViewById(R.id.buttonShowOverlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryShowOverlay();
            }
        });

        findViewById(R.id.buttonOpenInputMethodSelector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputMethodSelector();
            }
        });
    }

    private void showInputMethodSelector() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        InputMethodManager imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imeManager != null) {
            imeManager.showInputMethodPicker();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                 .show();
        }
    }


    private void tryShowOverlay() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                /** request permission via start activity for result */
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                showOverlay();
            }
        } else {
            showOverlay();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
        /** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    showOverlay();
                }
            }
        }
    }

    public void showOverlay() {
        Intent svc = new Intent(this, OverlayService.class);
        startService(svc);
        finish();
    }

}
