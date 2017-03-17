package esdip.pickapps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainService extends Service {

    private static final String DEBUG_TAG = "DEBUG";
    private static boolean debug = true;
    private static boolean trace = false;

    private static BroadcastReceiver location_broadcast_receiver;
    private static BroadcastReceiver weather_broadcast_receiver;

    // Location vars
    private static String latitude;
    private static String longitude;
    private static String city;

    // Weather vars
    private static String temperature;
    private static String text;
    private static Integer icon_id;
    private static String weather_active_image;

    // Active wallpaper image
    private static String active_image;

    private static SharedPreferences settings;
    private static Context context;
    private static String active_package;
    private static byte[] thumbnail_data;

    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final String IMAGES_PATH = "images";
    private static final String CONTENT_IMAGES_URI = "content://" + PROVIDER_NAME + "/" + IMAGES_PATH + "/image_name/%s/package_name/%s";

    // Package behaviors
    public static final String BEHAVIOR_NOTHING = "0";
    public static final String BEHAVIOR_WEATHER = "1";
    public static final String BEHAVIOR_DAY_WEEK = "2";
    public static final String BEHAVIOR_HOUR = "3";

    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(trace)Log.i(DEBUG_TAG, "MainService.onStartCommand");

        context = getBaseContext();

        // Literal to marking the active package
        active_package = getString(R.string.active_package);

        // Initialize settings
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        // Gather weather data
        Intent location_intent = new Intent(context, LocationIntentService.class);
        location_intent.setAction(LocationIntentService.ACTION_GET_LOCATION);
        context.startService(location_intent);

        // Weather widget
        Intent weather_widget_intent = new Intent(); //context, WeatherService.class
        weather_widget_intent.setAction(WeatherService.WEATHER_FETCHED);
        context.sendBroadcast(weather_widget_intent);
        Log.i(DEBUG_TAG, WeatherService.WEATHER_FETCHED);

                // Set wallaper behavior
        String current_active_behavior;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        Log.i(DEBUG_TAG, "hour of day: " + String.valueOf(hour));
        if(settings != null) {

            current_active_behavior = settings.getString(context.getString(R.string.active_behavior), "0");

            if(debug)Log.i(DEBUG_TAG, "onStartCommand.current_active_behavior: " + current_active_behavior);

            if(current_active_behavior.equals(BEHAVIOR_DAY_WEEK)){

                Intent dayweek_intent = new Intent(getBaseContext(), DayWeekWallpaperReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, dayweek_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            } else if(current_active_behavior.equals(BEHAVIOR_HOUR)){

                Intent hour_day_intent = new Intent(getBaseContext(), HourDayWallpaperReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, hour_day_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);

            }else{

                Intent weather_intent = new Intent(getBaseContext(), WeatherWallpaperReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, weather_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);

            }
        }

        // Alarm to gather weather data
        int update_frecuency = settings.getInt(getString(R.string.update_frecuency), 0);

        if( update_frecuency != 3 ){

            Intent weather_data_intent = new Intent(getBaseContext(), WeatherDataReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, weather_data_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) getBaseContext().getSystemService(getBaseContext().ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), ((update_frecuency + 1) * 60) * 60 * 1000, pendingIntent);

        }


        // Broadcaster for location service result
        location_broadcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String currentFlag = settings.getString("location_flag", "");
                String sendedFlag = intent.getStringExtra("location_flag");

                if (sendedFlag.equals(currentFlag)) {
                    if(trace)Log.i(DEBUG_TAG, "MainService.WeatherBroadcastReceiver - Misma peticion");
                } else {

                SharedPreferences.Editor edit = settings.edit();
                edit.putString("location_flag", sendedFlag);
                edit.apply();

                latitude = intent.getStringExtra(LocationIntentService.LOCATION_LATITUDE);
                longitude = intent.getStringExtra(LocationIntentService.LOCATION_LONGITUDE);
                city = intent.getStringExtra(LocationIntentService.LOCATION_CITY);

                    if(debug)Log.i(DEBUG_TAG, "MainService.LocationBroadcastReceiver city: " + city);

                //handleActionGetWeatherData();
                Intent weather_intent = new Intent(context, WeatherIntentService.class);

                weather_intent.setAction(WeatherIntentService.ACTION_GET_WEATHER_DATA);
                weather_intent.putExtra(LocationIntentService.LOCATION_LATITUDE, latitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_LONGITUDE, longitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_CITY, city);
                weather_intent.putExtra("flag", sendedFlag);

                context.startService(weather_intent);
            }
            }
        };

        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(location_broadcast_receiver, new IntentFilter(LocationIntentService.LOCATION_FETCHED));

        // Broadcaster for weather service result
        weather_broadcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(trace)Log.i(DEBUG_TAG, "MainService.WeatherBroadcastReceiver");

                String currentFlag = settings.getString("weather_flag", "");
                String sendedFlag = intent.getStringExtra("weather_flag");

                if(trace)Log.i(DEBUG_TAG, "currentFlag: " + currentFlag);
                if(trace)Log.i(DEBUG_TAG, "sendedFlag: " + sendedFlag);

                if (sendedFlag.equals(currentFlag)) {
                    if(trace)Log.i(DEBUG_TAG, "MainService.WeatherBroadcastReceiver - Misma peticion");
                } else {
                    // Receive weather data and update UI

                    SharedPreferences.Editor edit = settings.edit();
                    edit.putString("weather_flag", sendedFlag);
                    edit.apply();

                    temperature = intent.getStringExtra(WeatherIntentService.WEATHER_TEMPERATURE);
                    text = intent.getStringExtra(WeatherIntentService.WEATHER_TEXT);
                    icon_id = intent.getIntExtra(WeatherIntentService.WEATHER_ICON, -1);

                    weather_active_image = intent.getStringExtra(WeatherIntentService.WEATHER_IMAGE);
                }
            }
        };

        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(weather_broadcast_receiver, new IntentFilter(WeatherIntentService.WEATHER_FETCHED));

        if(Build.VERSION.SDK_INT < 17) {
            return START_STICKY;
        }else{
            return START_REDELIVER_INTENT;
        }
    }

    static void updateWallpaper(){

        if(trace)Log.i(DEBUG_TAG, "MainService.updateWallpaper");

        String current_active_package = "";

        if(settings != null) {
            current_active_package = settings.getString(context.getString(R.string.active_package), "");
        }

        if(debug)Log.i(DEBUG_TAG, "current_active_package: " + current_active_package);

        if(current_active_package != ""){

            active_image = getActiveImage();

            if(debug)Log.i(DEBUG_TAG, "active_image: " + active_image);

            Uri uri = Uri.parse(String.format(CONTENT_IMAGES_URI, active_image, current_active_package));
            CursorLoader cursorLoader = new CursorLoader(context, uri, null, null, null, null);
            Cursor c = cursorLoader.loadInBackground();

            try{

            if(c.getCount() > 0 && c.moveToFirst()) {

                while (!c.isAfterLast()) {

                    if (c.getBlob(c.getColumnIndex(DBManager.COLUMN_IMAGE_DATA)) != null) {

                        thumbnail_data = c.getBlob(c.getColumnIndex(DBManager.COLUMN_IMAGE_DATA));
                    }

                    c.moveToNext();
                }

                Bitmap thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);
            try {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                int density = Math.round(displayMetrics.density);
                int dpHeight =  displayMetrics.heightPixels;
                int dpWidth = displayMetrics.widthPixels;

                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

               if(Build.VERSION.SDK_INT < 20){

                   // Pre 5.0.0 versions
                   if(trace)Log.i(DEBUG_TAG, "density: " + String.valueOf(density) + " dpHeight: " + String.valueOf(dpHeight) + " dpwidth: " + String.valueOf(dpWidth));

                   if(dpWidth > 1000 && density > 2) {

                       // Large screens
                       //density: 3 dpHeight: 1920 dpwidth: 1080
                       Bitmap bitmap_canvas = Bitmap.createBitmap(dpWidth, dpHeight, Bitmap.Config.ARGB_8888);

                       Bitmap resizedBitmap = Bitmap.createScaledBitmap(thumbnail, dpWidth/2 + (dpWidth/20) + 25, dpHeight/2 + (dpHeight/20) + 30, false);

                       Canvas canvas = new Canvas(bitmap_canvas);
                       canvas.drawBitmap(bitmap_canvas, new Matrix(), null);
                       canvas.drawBitmap(resizedBitmap, dpWidth/4 - (dpWidth/30), dpHeight/4 - (dpHeight/30), null);

                       resizedBitmap.recycle();

                       wallpaperManager.setBitmap(bitmap_canvas);

                   }else{

                       // Small screens
                       Bitmap bitmap_canvas = Bitmap.createBitmap(dpWidth  * density, dpHeight  * density, Bitmap.Config.ARGB_8888);

                       Bitmap resizedBitmap = Bitmap.createScaledBitmap(thumbnail, dpWidth, dpHeight, false);

                       Canvas canvas = new Canvas(bitmap_canvas);
                       canvas.drawBitmap(bitmap_canvas, new Matrix(), null);
                       canvas.drawBitmap(resizedBitmap, dpWidth/2, dpHeight/2, null);

                       resizedBitmap.recycle();

                       wallpaperManager.setBitmap(bitmap_canvas);
                   }

               }else{

                   // Post 5.0.0

                   Bitmap wallpaper = Bitmap.createScaledBitmap(thumbnail,dpWidth,dpHeight, true);

                   wallpaperManager.setBitmap(wallpaper);
                   wallpaperManager.suggestDesiredDimensions(dpWidth, dpHeight);
               }

            } catch (IOException e) {
                Log.e(DEBUG_TAG, Log.getStackTraceString(e));
            }
        }
    } catch (Exception ex) {
        Log.e(DEBUG_TAG, Log.getStackTraceString(ex));
    }
        }else {
            if(debug)Log.i(DEBUG_TAG, "No hay ningún paquete activo");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(trace)Log.i(DEBUG_TAG, "onDestroy MainService");
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }


    private static String getActiveImage(){

        String current_active_behavior = settings.getString(context.getString(R.string.active_behavior), "0");
        Calendar calendar = Calendar.getInstance();

        switch (current_active_behavior){
            case BEHAVIOR_DAY_WEEK:

                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day) {
                    case Calendar.MONDAY:
                        return context.getString(R.string.monday);
                    case Calendar.TUESDAY:
                        return context.getString(R.string.tuesday);
                    case Calendar.WEDNESDAY:
                        return context.getString(R.string.wednesday);
                    case Calendar.THURSDAY:
                        return context.getString(R.string.thursday);
                    case Calendar.FRIDAY:
                        return context.getString(R.string.friday);
                    case Calendar.SATURDAY:
                        return context.getString(R.string.saturday);
                    case Calendar.SUNDAY:
                        return context.getString(R.string.sunday);
                }

            case BEHAVIOR_HOUR:

                return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));

            default:
                return weather_active_image;
        }
    }


    /************** ALARMS RECEIVERS ***********/

    public static class WeatherDataReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(trace) Log.i(DEBUG_TAG, "WeatherDataReceiver.onReceive");

            // Request for LocationIntentService
            Intent location_intent = new Intent(context, LocationIntentService.class);
            location_intent.setAction(LocationIntentService.ACTION_GET_LOCATION);
            context.startService(location_intent);
        }

    }

    public static class WeatherWallpaperReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            active_image = getActiveImage();
            updateWallpaper();
        }

    }

    public static class DayWeekWallpaperReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

           active_image = getActiveImage();
           updateWallpaper();
        }
    }

    public static class HourDayWallpaperReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            active_image = getActiveImage();
            updateWallpaper();
        }

    }

}