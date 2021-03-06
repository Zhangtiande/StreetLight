package com.app.streetlight;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.app.streetlight.Device.Device;
import com.app.streetlight.Device.GetDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * helper methods.
 */
public class WarnService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_WARN = "com.app.streetlight.action.Warning";


    private static final String CHANNEL_ID = "Warning";
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private NotificationManager nm;
    private Future<List<Device>> future;
    private List<Device> devices = new ArrayList<>();

    public WarnService() {
        super("WarnService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionWarn(Context context) {
        Intent intent = new Intent(context, WarnService.class);
        intent.setAction(ACTION_WARN);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WARN.equals(action)) {
                handleActionFoo();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        List<String> strings = new ArrayList<>();
        while (true) {
            if (strings.size() > 0) {
                strings.clear();
            }
            GetDevice getDevice = new GetDevice();
            future = executorService.submit(getDevice);
            try {
                devices = future.get();
                devices.forEach(device -> {
                    if (device.getStatus().equals("OFFLINE")) {
                        strings.add(device.getDeviceName() + "?????????????????????????????????????????????\n");
                    }
                });
                Intent notifyIntent = new Intent(this, MainActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                        this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                String name = "test channel";
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("??????????????????");
                nm.createNotificationChannel(channel);
                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
                strings.forEach(style::addLine);
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentIntent(notifyPendingIntent)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(strings.size() + "?????????????????????")
                        .setStyle(style)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // notificationId is a unique int for each notification that you must define
                int notificationId = 1;
                notificationManager.notify(notificationId, notification);

                devices.clear();

                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}