package es.shyri.touchmapper.overlay;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import es.shyri.touchmapper.R;

public class OverlayService extends Service {
    private static final String TAG = OverlayService.class.getSimpleName();

    private final int notificationId = 32345;

    private WindowManager windowManager;

    private DisplayMetrics metrics;

    private Notification notification;

    private OverlayView mapperOverlay;

    private LayoutParams params;


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);

        chan.setLightColor(Color.CYAN);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);

        return channelId;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);


        notification = createNotification();

        startForeground(notificationId, notification);

        addOverlayView();
    }

    private Notification createNotification() {
        final String appName = getString(R.string.app_name);

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(appName, "Overlay Service");
        } else {
            // If earlier version channel ID is not used
            // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
            channelId = appName;
        }

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.getPackageName()));

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle(appName)
                .setOngoing(true)
                .setContentText(getString(R.string.svc_notif))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_gamepad)
                .setContentIntent(resultPendingIntent)
                .build();
    }

    private void addOverlayView() {

        mapperOverlay = new MapperOverlay(this, windowManager);

        params = mapperOverlay.getParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        mapperOverlay.setParams(params);


        mapperOverlay.addFloatyView();
    }


    @Override
    public void onDestroy() {

        super.onDestroy();

        if (mapperOverlay != null) {

            mapperOverlay.removeFloatyView();
            mapperOverlay = null;
        }
    }



}
