package esdip.pickapps;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ClockService extends Service {

    private static final String DEBUG_TAG = "DEBUG";
    public static BroadcastReceiver timeReceiver;

    public ClockService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(DEBUG_TAG, "ClockService: onStartCommand");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                timeReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i(DEBUG_TAG, "Time Service onReceive");

                        // Update UI
                        // TODO: enviar señal de vuelta
                       // esdip.clockwidget.ClockReceiver.setCalendarData();
                        //esdip.clockwidget.ClockReceiver.updateWidget(context);
                    }
                };
                getApplicationContext().registerReceiver(timeReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
            }
        };

        Thread WeatherServiceThread = new Thread(r);
       // WeatherServiceThread.start();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
