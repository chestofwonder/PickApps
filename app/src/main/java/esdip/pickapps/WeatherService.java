package esdip.pickapps;

// Madrid: Latitud 40.25 N, Longitud 3.41 O

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class WeatherService extends Service {

    private static final String DEBUG_TAG = "DEBUG";

    // Current weather vars
    public static String TEMPERATURE;
    public static String TEXT;
    public static String CODE;
    public static Integer ICON_ID;



    // Forecast info vars
    public static ArrayList<String> next_hours_timestamps = new ArrayList<>();
    public static ArrayList<String> next_hours_icons = new ArrayList<>();
    public static ArrayList<String> next_hours_temperatures = new ArrayList<>();
    public static ArrayList<String> next_days_timestamps = new ArrayList<>();
    public static ArrayList<String> next_days_icons = new ArrayList<>();
    public static ArrayList<String> next_days_temperatures_min = new ArrayList<>();
    public static ArrayList<String> next_days_temperatures_max = new ArrayList<>();

    public static final String FORECAST_API = "https://api.forecast.io/forecast/%s/%s,%s?lang=%s&units=%s";

    //private static final String YAHOO_YQL_QUERY = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text='madrid,%20es')%20and%20u='c'&format=json";
    //private static final String YAHOO_YQL_QUERY = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20%28select%20woeid%20from%20geo.places%281%29%20where%20text%3D%27madrid%2C%20es%27%29%20and%20u%3D%27c%27&format=json&diagnostics=true&callback=";
    //private static final String YAHOO_YQL_WEATHER_QUERY = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%3D766273%20and%20u%3D'c'&format=json&diagnostics=true&callback=";
    //private static final String YAHOO_YQL_WOEID_QUERY = "https://query.yahooapis.com/v1/public/yql?q=select%20woeid%20from%20geo.places(1)%20where%20text%3D'madrid%20es'&format=json&diagnostics=true&callback=";

    //private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=1110ebd8168d2faf168c3c4d6a17d809&units=metric";


    public static String API_KEY;
    public static String UNITS;
    public static String LANGUAGE;

    private static LocalBroadcastManager broadcaster;
    //private static BroadcastManager broadcaster;
    public static final String WEATHER_FETCHED = "esdip.pickapps.WEATHER_FETCHED";
    public static final String WEATHER_TEXT = "esdip.pickapps.WEATHER_TEXT";
    public static final String WEATHER_TEMPERATURE = "esdip.pickapps.WEATHER_TEMPERATURE";
    public static final String WEATHER_ICON = "esdip.pickapps.WEATHER_ICON";

    public static String LATITUDE;
    public static String LONGITUDE;

    private static SharedPreferences settings;
    private static Context context;

    // Fetch strings for weather conditions
    static String sunny;
    static String cloudy_night;
    static String cloudy_day;
    static String moon;
    static String rainy_day;
    static String rain_night;
    static String active_image;

    static String active_package;
    static String network_not_available;

    private static byte[] thumbnail_data;

    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/packages");
    private static final Uri CONTENT_IMAGES_URI = Uri.parse("content://" + PROVIDER_NAME + "/images");


    public WeatherService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(DEBUG_TAG, "onStartCommand weather service");

        broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
        context = getBaseContext();

        LANGUAGE = getString(R.string.language);
        API_KEY = getString(R.string.forecast_key);
        UNITS = getString(R.string.weather_units);

        sunny = getString(R.string.sunny);
        cloudy_night = getString(R.string.cloudy_night);
        cloudy_day = getString(R.string.cloudy_day);
        moon = getString(R.string.moon);
        rainy_day = getString(R.string.rain_day);
        rain_night = getString(R.string.rain_night);

        active_package = getString(R.string.active_package);
        network_not_available = getString(R.string.network_not_available);

        // Get active package
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        updateWeather(getBaseContext());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(DEBUG_TAG, "onDestroy weather service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static void sendResult(){

        Intent intent = new Intent(WEATHER_FETCHED);

        if( CODE == null ) {
            // Default values
            TEXT = "Obteniendo la información del tiempo ...";
            TEMPERATURE = "N/A";
            ICON_ID = null;
        }

        //updateWallpaper();

        intent.putExtra(WEATHER_TEXT, TEXT);
        intent.putExtra(WEATHER_TEMPERATURE, TEMPERATURE);
        intent.putExtra(WEATHER_ICON, ICON_ID);

        intent.setAction(WEATHER_FETCHED);
        context.sendBroadcast(intent);


       // broadcaster.sendBroadcast(intent);
    }

    static void updateWeather(Context context){

        if(isNetworkConnected(context)) {

            LATITUDE = "40.25";
            LONGITUDE = "-3.41";

            Intent intent = new Intent(context, WeatherIntentService.class);
            intent.setAction(WeatherIntentService.ACTION_GET_WEATHER_DATA);
            intent.putExtra(WeatherIntentService.API_KEY, API_KEY);
            intent.putExtra(WeatherIntentService.UNITS, UNITS);
            intent.putExtra(WeatherIntentService.LANGUAGE, LANGUAGE);
            context.startService(intent);
        }else{
            Toast.makeText(context, network_not_available, Toast.LENGTH_SHORT).show();
        }

        setWeatherState();
        sendResult();
    }


    static void setWeatherState() {

        if(CODE == null){
            active_image = sunny;
            ICON_ID = -1;
        }else {
            //Log.i(DEBUG_TAG, "code recibido: " + code);
            switch (CODE) {
                case "clear-day":
                    ICON_ID = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "clear-night":
                    ICON_ID = R.drawable.moon;
                    active_image = moon;
                    break;
                case "partly-cloudy-day":
                    ICON_ID = R.drawable.cloudy;
                    active_image = cloudy_day;
                    break;
                case "partly-cloudy-night":
                    ICON_ID = R.drawable.cloudy_night;
                    active_image = cloudy_night;
                    break;
                case "cloudy":
                    ICON_ID = R.drawable.cloudy;
                    active_image = cloudy_day;
                    break;
                case "rain":
                    ICON_ID = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "sleet":
                    ICON_ID = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "snow":
                    ICON_ID = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "wind":
                    ICON_ID = R.drawable.cloudy;
                    active_image = cloudy_day;
                    break;
                default:
                    active_image = sunny;
                    break;
            }
        }
    }

    /*static void setWeatherState() {

        if(code == null){
            active_image = sunny;
            icon_id = -1;
        }else {
            switch (code) {
                case "0": //tornado
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "1": //tropical storm
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "2": //hurricane
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "3": //severe thunderstorms
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "4": //thunderstorms
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "5": //mixed rain and snow
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "6": //mixed rain and sleet //lluvia y aguanieve
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "7": //mixed snow and sleet //nieve y aguanieve
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "8": //freezing drizzle //llovizna helada
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "9": //drizzle //llovizna
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "10": //freezing rain
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "11": //showers //chubascos
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "12": //showers //chubascos
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "13": //snow flurries //copos de nieve
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "14": //light snow showers
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "15": //blowing snow //soplo de nieve
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "16": //snow
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "17": //hail //granizo
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "18": //sleet //aguanieve
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "19": //dust
                    icon_id = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "20": //foggy
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "21": //haze //neblina
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "22": //smoky
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "23": //blustery //borrascoso
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "24": //windy
                    icon_id = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "25": //cold
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "26": //cloudy
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "27": //mostly cloudy (night)
                    icon_id = R.drawable.cloudy_night;
                    active_image = cloudy_night;
                    break;
                case "28": //mostly cloudy (day)
                    icon_id = R.drawable.mostly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "29": //partly cloudy (night)
                    icon_id = R.drawable.partly_cloudy_night;
                    active_image = cloudy_night;
                    break;
                case "30": //partly cloudy (day)
                    icon_id = R.drawable.partly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "31": //clear(night)
                    icon_id = R.drawable.moon;
                    active_image = moon;
                    break;
                case "32": //sunny
                    icon_id = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "33": //fair(night) //despejado
                    icon_id = R.drawable.moon;
                    active_image = moon;
                    break;
                case "34": //fair(day)
                    icon_id = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "35": //mixed rain and hail
                    icon_id = R.drawable.showers;
                    active_image = rainy_day;
                    break;
                case "36": //hot
                    icon_id = R.drawable.sunny;
                    active_image = sunny;
                    break;
                case "37": //isolated thunderstorms //tormentas aisladas
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "38": //scattered thunderstorms
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "39": //scattered thunderstorms //tormentas eléctricas dispersas
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "40": //scattered showers //chubascos dispersos
                    icon_id = R.drawable.mostly_cloudy_rain;
                    active_image = rainy_day;
                    break;
                case "41": //heavy snow //fuertes nevadas
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "42": //scattered snow showers //chubascos de nieve dispersos
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "43": //heavy snow
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "44": //partly cloudy //parcialmente nublado
                    icon_id = R.drawable.partly_cloudy;
                    active_image = cloudy_day;
                    break;
                case "45": //thundershowers //tormentosos
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "46": //snow showers
                    icon_id = R.drawable.snow;
                    active_image = rainy_day;
                    break;
                case "47": //isolated thundershowers //chubascos aislados
                    icon_id = R.drawable.thunderstorms;
                    active_image = rainy_day;
                    break;
                case "3200": //not available
                    break;
                default:
                    active_image = sunny;
                    break;
            }
        }
    }*/

    static void updateWallpaper(){

        Log.i(DEBUG_TAG, "updateWallpaper");

        String current_active_package = "";

        if(settings != null) {
            current_active_package = settings.getString("active_package", "");
        }

        if(current_active_package != ""){

            Uri uri = Uri.parse(CONTENT_IMAGES_URI + "/image_name/" + active_image + "/package_name/" + current_active_package);
            CursorLoader cursorLoader = new CursorLoader(context, uri, null, null, null, null);
            Cursor c = cursorLoader.loadInBackground();

            c.moveToFirst();

            while (!c.isAfterLast()) {

                if(c.getBlob(c.getColumnIndex(DBManager.COLUMN_IMAGE_DATA)) != null){

                    //  byte[] thumbnail_data = c.getBlob(c.getColumnIndex(DBManager.COLUMN_IMAGE_DATA));
                    // Bitmap thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);

                    thumbnail_data = c.getBlob(c.getColumnIndex(DBManager.COLUMN_IMAGE_DATA));



                           /* DisplayMetrics displayMetrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                            int height = displayMetrics.heightPixels;
                            int width = displayMetrics.widthPixels << 1;*/
                    //480 x 800 pixels (~233 ppi pixel density)

                    //por el dispositivo:
                    //320 x 533
                    //densidad: 1,5

                   /* Toast.makeText(context, "ancho pantalla: " + dpWidth, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "alto pantalla: " + dpHeight, Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "densidad pantalla: " + displayMetrics.density, Toast.LENGTH_LONG).show();*/




                   /* try {
                        final Drawable drawable = this.mImageView.getDrawable();


                        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                        return bitmap;
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                        return null;
                    }


                    Bitmap tmp_thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);
                    Bitmap thumbnail = Bitmap.createScaledBitmap(tmp_thumbnail, dpWidth / density, dpHeight / density, true);
                    tmp_thumbnail.recycle();
                    Canvas canvas = new Canvas(thumbnail);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);

                    try {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(app_context);
                        wallpaperManager.suggestDesiredDimensions(dpWidth, dpHeight);

                        wallpaperManager.setBitmap(thumbnail);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/







                    // -->  BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
                    // -->  decodeOptions.inJustDecodeBounds = true;

                    //Bitmap tmp_thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);
                // ->    Bitmap thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);
                   // Bitmap thumbnail = Bitmap.createScaledBitmap(tmp_thumbnail, dpWidth / density, dpHeight / density, true);
                    //tmp_thumbnail.recycle();
                           /* Log.i(DEBUG_TAG, "options.outWidth: " + decodeOptions.outWidth);
                            Log.i(DEBUG_TAG, "options.outHeight: " + decodeOptions.outHeight);
                            Log.i(DEBUG_TAG, "thumbnail: " + thumbnail.getByteCount());*/
                    //   try {



                   // --> try {
                    // -->   WallpaperManager wallpaperManager = WallpaperManager.getInstance(app_context);
                        ;//returned 1280 on s3
                       ;//also returned 1280 on s3
                       // Toast.makeText(context, "ancho pantalla deseable: " + wallpaperManager.getDesiredMinimumWidth(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(context, "alto pantalla deseable: " +  wallpaperManager.getDesiredMinimumHeight(), Toast.LENGTH_LONG).show();
                       // wallpaperManager.clear();
                        //  BitmapFactory.decodeResource(getResources(), wallpapersPagerAdapter.getWallpaperId(position), options);
                       // wallpaperManager.suggestDesiredDimensions(dpWidth, dpHeight);

                    // -->                     wallpaperManager.setBitmap(thumbnail);
                    // --> } catch (IOException e) {
                    // -->     e.printStackTrace();
                    // -->  }
                         /*   } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                }

                c.moveToNext();
            }


            Bitmap thumbnail = BitmapFactory.decodeByteArray(thumbnail_data, 0, thumbnail_data.length);

          /*  density = Math.round(displayMetrics.density);
            dpHeight =  displayMetrics.heightPixels;
            dpWidth = displayMetrics.widthPixels;*/

            try {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                // int dpHeight =  Math.round(displayMetrics.heightPixels / displayMetrics.density);
                //int density = Math.round(displayMetrics.density);
                int dpHeight =  displayMetrics.heightPixels;
                int dpWidth = displayMetrics.widthPixels;

                Bitmap wallpaper = Bitmap.createScaledBitmap(thumbnail,dpWidth,dpHeight, true);
                //Canvas canvas = new Canvas(wallpaper);



                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

                wallpaperManager.setBitmap(wallpaper);
                wallpaperManager.suggestDesiredDimensions(dpWidth, dpHeight);


//Toast.makeText(context, "dpWidth: " + String.valueOf(dpWidth) + "dpHeight: " + String.valueOf(dpHeight), Toast.LENGTH_LONG).show();
// fisico 480 x 800
// virtual 730 x 1278

            } catch (IOException e) {
                e.printStackTrace();
            }


        }else {
            Log.i(DEBUG_TAG, "No hay ningún paquete activo");
        }
    }

    static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        if(null != cm.getActiveNetworkInfo()){
            //Log.i(DEBUG_TAG, cm.getActiveNetworkInfo().toString());
            return true;
        }

        return false;
    }

}
