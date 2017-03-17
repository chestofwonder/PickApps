package esdip.pickapps;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


// Intent service for fetch weather data asyncronously
// Is called from WeatherService

public class WeatherIntentService extends IntentService {

    private static final String DEBUG_TAG = "DEBUG";
    private static boolean debug = false;
    private static boolean test = false;


    public static final String WEATHER_FETCHED = "esdip.pickapps.WEATHER_FETCHED";

    public static final String ACTION_GET_WEATHER_DATA = "esdip.pickapps.action.get_weather_data";

    /*public static final String CONSTANT_FORECAST_CLEAR_DAY = "clear-day";
    public static final String CONSTANT_FORECAST_CLEAR_NIGHT = "clear-night";
    public static final String CONSTANT_FORECAST_PARTLY_CLOUDY_DAY = "partly-cloudy-day";
    public static final String CONSTANT_FORECAST_PARTLY_CLOUDY_NIGHT = "partly-cloudy-night";
    public static final String CONSTANT_FORECAST_CLOUDY = "cloudy";
    public static final String CONSTANT_FORECAST_RAIN = "rain";
    public static final String CONSTANT_FORECAST_SLEET = "sleet";
    public static final String CONSTANT_FORECAST_SNOW = "snow";*/

   /* public static final String CONSTANT_CLEAR_DAY = "sunny";
    public static final String CONSTANT_CLEAR_NIGHT = "moon";
    public static final String CONSTANT_PARTLY_CLOUDY_DAY = "partly_cloudy";
    public static final String CONSTANT_PARTLY_CLOUDY_NIGHT = "partly_cloudy_night";
    public static final String CONSTANT_CLOUDY = "cloudy";
    public static final String CONSTANT_RAIN = "showers";
    public static final String CONSTANT_SLEET = "sleet";
    public static final String CONSTANT_SNOW = "snow";*/

    public static final String CONSTANT_SOL = "01_sol";
    public static final String CONSTANT_LUNA = "02_luna";
    public static final String CONSTANT_NUBOSO_DIA = "03_nuboso_dia";
    public static final String CONSTANT_NUBOSO_NOCHE = "04_nuboso_noche";
    public static final String CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_DIA = "05_intervalos_nubosos_con_lluvia_dia";
    public static final String CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_NOCHE = "06_intervalos_nubosos_con_lluvia_noche";
    public static final String CONSTANT_MUY_NUBOSO_DIA = "07_muy_nuboso_dia";
    public static final String CONSTANT_MUY_NUBOSO_NOCHE = "08_muy_nuboso_noche";
    public static final String CONSTANT_NIEBLA_DIA = "09_niebla_dia";
    public static final String CONSTANT_NIEBLA_NOCHE = "10_niebla_noche";
    public static final String CONSTANT_CUBIERTO_CON_LLUVIA_DIA = "11_cubierto_con_lluvia_dia";
    public static final String CONSTANT_CUBIERTO_CON_LLUVIA_NOCHE = "12_cubierto_con_lluvia_noche";
    public static final String CONSTANT_TORMENTA_DIA = "13_tormenta_dia";
    public static final String CONSTANT_TORMENTA_NOCHE = "14_tormenta_noche";
    public static final String CONSTANT_NIEVE_DIA = "15_nieve_dia";
    public static final String CONSTANT_NIEVE_NOCHE = "16_nieve_noche";


    private static String latitude;
    private static String longitude;
    private static String city;

    public static String API_KEY;
    public static String UNITS;
    public static String LANGUAGE;

    static String sunny;
    static String cloudy_night;
    static String cloudy_day;
    static String moon;
    static String rainy_day;
    static String rain_night;

    // Forecast info vars
    public static ArrayList<String> next_hours_timestamps = new ArrayList<>();
    public static ArrayList<Integer> next_hours_icons = new ArrayList<>();
    public static ArrayList<String> next_hours_temperatures = new ArrayList<>();
    public static ArrayList<String> next_days_timestamps = new ArrayList<>();
    public static ArrayList<Integer> next_days_icons = new ArrayList<>();
    public static ArrayList<String> next_days_temperatures_min = new ArrayList<>();
    public static ArrayList<String> next_days_temperatures_max = new ArrayList<>();
    public static ArrayList<String> next_days_sunrises = new ArrayList<>();
    public static ArrayList<String> next_days_sunsets = new ArrayList<>();

    private static final String FORECAST_API = "https://api.forecast.io/forecast/%s/%s,%s?lang=%s&units=%s";

    public static String CODE;
    public static String TEMPERATURE;
    public static String TEXT;
    public static Integer ICON_ID;
    public static String IMAGE;

    public static String TEMPERATURE_MIN;
    public static String TEMPERATURE_MAX;
    public static String HUMIDITY;
    public static String WINDSPEED;
    public static String CLOUDCOVER;
    public static String PRESSURE;
    public static String SUNRISETIME;
    public static String SUNSETTIME;

    public static String UPDATE;

    private static SharedPreferences settings;

    public static final String WEATHER_TEXT = "esdip.pickapps.WEATHER_TEXT";
    public static final String WEATHER_TEMPERATURE = "esdip.pickapps.WEATHER_TEMPERATURE";
    public static final String WEATHER_ICON = "esdip.pickapps.WEATHER_ICON";
    public static final String WEATHER_IMAGE = "esdip.pickapps.WEATHER_IMAGE";
    public static final String WEATHER_TEMPERATURE_MIN = "esdip.pickapps.WEATHER_TEMPERATURE_MIN";
    public static final String WEATHER_TEMPERATURE_MAX = "esdip.pickapps.WEATHER_TEMPERATURE_MAX";
    public static final String WEATHER_HUMIDITY = "esdip.pickapps.WEATHER_HUMIDITY";
    public static final String WEATHER_WINDSPEED = "esdip.pickapps.WEATHER_WINDSPEED";
    public static final String WEATHER_CLOUDCOVER = "esdip.pickapps.WEATHER_CLOUDCOVER";
    public static final String WEATHER_PRESSURE = "esdip.pickapps.WEATHER_PRESSURE";
    public static final String WEATHER_SUNRISETIME = "esdip.pickapps.WEATHER_SUNRISETIME";
    public static final String WEATHER_SUNSETTIME = "esdip.pickapps.WEATHER_SUNSETTIME";

    public static String WEATHER_UPDATE = "esdip.pickapps.WEATHER_UPDATE";

    // Local broadcaster to send weather data
    private static LocalBroadcastManager broadcaster;

    public WeatherIntentService() {
        super("WeatherIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        API_KEY = getString(R.string.forecast_key);
        UNITS = getString(R.string.weather_units);
        LANGUAGE = getString(R.string.language);

        sunny = getString(R.string.sunny);
        cloudy_night = getString(R.string.cloudy_night);
        cloudy_day = getString(R.string.cloudy_day);
        moon = getString(R.string.moon);
        rainy_day = getString(R.string.rain_day);
        rain_night = getString(R.string.rain_night);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static ArrayList<String> getWeatherConstants(){

        ArrayList<String> WeatherConstants = new ArrayList<>();

       /* WeatherConstants.add(CONSTANT_CLEAR_DAY);
        WeatherConstants.add(CONSTANT_CLEAR_NIGHT);
        WeatherConstants.add(CONSTANT_PARTLY_CLOUDY_DAY);
        WeatherConstants.add(CONSTANT_PARTLY_CLOUDY_NIGHT);
        WeatherConstants.add(CONSTANT_CLOUDY);
        WeatherConstants.add(CONSTANT_RAIN);*/
        //WeatherConstants.add(CONSTANT_SLEET);
        /*WeatherConstants.add(CONSTANT_SNOW);*/


        WeatherConstants.add(CONSTANT_SOL);
        WeatherConstants.add(CONSTANT_LUNA);
        WeatherConstants.add(CONSTANT_NUBOSO_DIA);
        WeatherConstants.add(CONSTANT_NUBOSO_NOCHE);
        WeatherConstants.add(CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_DIA);
        WeatherConstants.add(CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_NOCHE);
        WeatherConstants.add(CONSTANT_MUY_NUBOSO_DIA);
        WeatherConstants.add(CONSTANT_MUY_NUBOSO_NOCHE);
        WeatherConstants.add(CONSTANT_NIEBLA_DIA);
        WeatherConstants.add(CONSTANT_NIEBLA_NOCHE);
        WeatherConstants.add(CONSTANT_CUBIERTO_CON_LLUVIA_DIA);
        WeatherConstants.add(CONSTANT_CUBIERTO_CON_LLUVIA_NOCHE);
        WeatherConstants.add(CONSTANT_TORMENTA_DIA);
        WeatherConstants.add(CONSTANT_TORMENTA_NOCHE);
        WeatherConstants.add(CONSTANT_NIEVE_DIA);
        WeatherConstants.add(CONSTANT_NIEVE_NOCHE);

        return WeatherConstants;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(debug)Log.i(DEBUG_TAG, "onHandleIntent WeatherIntentService");

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_WEATHER_DATA.equals(action)) {
                //final String latitude = intent.getStringExtra(LATITUDE);
                //final String longitude = intent.getStringExtra(LONGITUDE);
                //final String api_key = intent.getStringExtra(API_KEY);
                //final String units = intent.getStringExtra(UNITS);
                //final String language = intent.getStringExtra(LANGUAGE);
                latitude = intent.getStringExtra(LocationIntentService.LOCATION_LATITUDE);
                longitude = intent.getStringExtra(LocationIntentService.LOCATION_LONGITUDE);
                city = intent.getStringExtra(LocationIntentService.LOCATION_CITY);

                handleActionGetWeatherData();
            }else{
                if(debug)Log.i(DEBUG_TAG, "onHandleIntent Acción " + action + " no implementada");
            }
        }
    }


    private void handleActionGetWeatherData() {

        if(debug)Log.i(DEBUG_TAG, "handleActionGetWeatherData - latitude: " + latitude + " longitude: " + longitude);

        if(null != latitude && null != longitude){

            if(debug) Log.i(DEBUG_TAG, "Haciendo la peticion de fetchWeatherData");

            final JSONObject json = fetchWeatherData();
            if(null != json){
                parseWeatherData(json);
            }
        }
    }

    private JSONObject fetchWeatherData() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if(null == latitude || null == longitude){
                return null;
            }

            URL url = new URL(String.format(FORECAST_API, API_KEY, latitude, longitude, LANGUAGE, UNITS));

            if(debug)Log.i(DEBUG_TAG, String.valueOf(url));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);

            if(null == json){
                return null;
            }

            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            //Log.i(DEBUG_TAG, "longitud del json: " + String.valueOf(json.length()));

            JSONObject data = new JSONObject(json.toString());

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseWeatherData(JSONObject json) {
        try {
            //Log.i(DEBUG_TAG, "renderData Forecast");

            // Current weather
            JSONObject json_currently = json.getJSONObject("currently");

            CODE = json_currently.getString("icon");
            TEMPERATURE = json_currently.getString("temperature");
            TEXT = json_currently.getString("summary");
            HUMIDITY = json_currently.getString("humidity");
            WINDSPEED = json_currently.getString("windSpeed");
            CLOUDCOVER = json_currently.getString("cloudCover");
            PRESSURE = json_currently.getString("pressure");

            if(debug)Log.i(DEBUG_TAG, "code: " + CODE);
            if(debug)Log.i(DEBUG_TAG, "temperature: " + TEMPERATURE);
            if(debug)Log.i(DEBUG_TAG, "text: " + TEXT);
            if(debug)Log.i(DEBUG_TAG, "HUMIDITY: " + HUMIDITY);
            if(debug)Log.i(DEBUG_TAG, "WINDSPEED: " + WINDSPEED);
            if(debug)Log.i(DEBUG_TAG, "CLOUDCOVER: " + CLOUDCOVER);
            if(debug)Log.i(DEBUG_TAG, "PRESSURE: " + PRESSURE);

            // Forecast weather
            next_hours_timestamps.clear();
            next_hours_icons.clear();
            next_hours_temperatures.clear();
            next_days_timestamps.clear();
            next_days_icons.clear();
            next_days_temperatures_min.clear();
            next_days_temperatures_max.clear();
            next_days_sunrises.clear();
            next_days_sunsets.clear();

            // Hourly forecast
            JSONObject json_hourly = json.getJSONObject("hourly");
            JSONArray json_data = json_hourly.getJSONArray("data");

            for(int i = 0 ; i < json_data.length(); i++){

                JSONObject json_hour = json_data.getJSONObject(i);
                next_hours_timestamps.add(json_hour.getString("time"));
                next_hours_icons.add(getWeatherIcon(json_hour.getString("icon")));
                next_hours_temperatures.add(json_hour.getString("temperature"));
            }

            // Daily forecast
            JSONObject json_daily = json.getJSONObject("daily");
            JSONArray json_daily_data = json_daily.getJSONArray("data");

            for(int i = 0 ; i < json_daily_data.length(); i++){

                JSONObject json_day = json_daily_data.getJSONObject(i);

                next_days_timestamps.add(json_day.getString("time"));
                next_days_icons.add(getWeatherIcon(json_day.getString("icon")));
                next_days_temperatures_min.add(json_day.getString("temperatureMin"));
                next_days_temperatures_max.add(json_day.getString("temperatureMax"));
                next_days_sunrises.add(json_day.getString("sunriseTime"));
                next_days_sunsets.add(json_day.getString("sunsetTime"));
            }

            // Get data for current weather, contained in forecast data
            SUNRISETIME = next_days_sunrises.get(1);
            SUNSETTIME = next_days_sunsets.get(1);
            TEMPERATURE_MIN = next_days_temperatures_min.get(1);
            TEMPERATURE_MAX = next_days_temperatures_max.get(1);

            if(debug)Log.i(DEBUG_TAG, "SUNRISETIME: " + SUNRISETIME);
            if(debug)Log.i(DEBUG_TAG, "SUNSETTIME: " + SUNSETTIME);
            if(debug)Log.i(DEBUG_TAG, "TEMPERATURE_MIN: " + TEMPERATURE_MIN);
            if(debug)Log.i(DEBUG_TAG, "TEMPERATURE_MAX: " + TEMPERATURE_MAX);

            setWeatherState();
            sendResult();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setWeatherState() {

        if(CODE == null){
            IMAGE = CONSTANT_SOL;
            ICON_ID = -1;
        }else {
            //Log.i(DEBUG_TAG, "code recibido: " + code);
            switch (CODE) {
                case "clear-day":
                    ICON_ID = R.drawable.sunny;
                    IMAGE = CONSTANT_SOL; //sunny;
                    break;
                case "clear-night":
                    ICON_ID = R.drawable.moon;
                    IMAGE = CONSTANT_LUNA;
                    break;
                case "partly-cloudy-day":
                    ICON_ID = R.drawable.cloudy;
                    IMAGE = CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_DIA;
                    break;
                case "partly-cloudy-night":
                    ICON_ID = R.drawable.cloudy_night;
                    IMAGE = CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_NOCHE;
                    break;
                case "cloudy":
                    ICON_ID = R.drawable.cloudy;
                    IMAGE = CONSTANT_MUY_NUBOSO_DIA;
                    break;
                case "rain":
                    ICON_ID = R.drawable.showers;
                    IMAGE = CONSTANT_TORMENTA_DIA;
                    break;
                case "sleet":
                    ICON_ID = R.drawable.showers;
                    IMAGE = CONSTANT_TORMENTA_DIA;
                    break;
                case "snow":
                    ICON_ID = R.drawable.snow;
                    IMAGE = CONSTANT_NIEVE_DIA;
                    break;
                case "wind":
                    ICON_ID = R.drawable.sunny;
                    IMAGE = CONSTANT_SOL;
                    break;
                default:
                    IMAGE = CONSTANT_SOL;
                    break;
            }
        }
    }

    static Integer getWeatherIcon(String weather_code) {

        if(weather_code == null){
            return -1;
        }else {
            switch (weather_code) {
                case "clear-day":
                    return R.drawable.sunny;
                case "clear-night":
                    return R.drawable.moon;
                case "partly-cloudy-day":
                    return R.drawable.cloudy;
                case "partly-cloudy-night":
                    return R.drawable.cloudy_night;
                case "cloudy":
                    return R.drawable.cloudy;
                case "rain":
                    return R.drawable.showers;
                case "sleet":
                    return R.drawable.showers;
                case "snow":
                    return R.drawable.snow;
                case "wind":
                    return R.drawable.cloudy;
                default:
                    return -1;
            }
        }
    }

    private void sendResult(){

        Intent intent = new Intent(WEATHER_FETCHED);

        if( CODE == null ) {
            // Default values
            TEXT = "Obteniendo la información del tiempo ...";
            TEMPERATURE = "N/A";
            ICON_ID = null;
        }

        if(test) {
            Random r = new Random();
            int Low = 1;
            int High = 9;
            int Result = r.nextInt(High - Low) + Low;

            switch (Result) {
                case 1:
                    ICON_ID = R.drawable.sunny;
                    IMAGE = CONSTANT_SOL; //sunny;
                    break;
                case 2:
                    ICON_ID = R.drawable.moon;
                    IMAGE = CONSTANT_LUNA;
                    break;
                case 3:
                    ICON_ID = R.drawable.cloudy;
                    IMAGE = CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_DIA;
                    break;
                case 4:
                    ICON_ID = R.drawable.cloudy_night;
                    IMAGE = CONSTANT_INTERVALOS_NUBOSOS_CON_LLUVIA_NOCHE;
                    break;
                case 5:
                    ICON_ID = R.drawable.cloudy;
                    IMAGE = CONSTANT_MUY_NUBOSO_DIA;
                    break;
                case 6:
                    ICON_ID = R.drawable.showers;
                    IMAGE = CONSTANT_TORMENTA_DIA;
                    break;
                case 7:
                    ICON_ID = R.drawable.showers;
                    IMAGE = CONSTANT_TORMENTA_DIA;
                    break;
                case 8:
                    ICON_ID = R.drawable.snow;
                    IMAGE = CONSTANT_NIEVE_DIA;
                    break;
                case 9:
                    ICON_ID = R.drawable.sunny;
                    IMAGE = CONSTANT_SOL;
                    break;
            }
        }
        UPDATE = String.valueOf(Calendar.getInstance().getTimeInMillis());

        Random rn = new Random();

        SharedPreferences.Editor edit = settings.edit();
        edit.putString("weather_flag", "");
        edit.apply();

        intent.putExtra(WEATHER_ICON, ICON_ID);
        intent.putExtra(WEATHER_TEMPERATURE, TEMPERATURE);
        intent.putExtra(WEATHER_TEXT, TEXT);
        intent.putExtra(WEATHER_IMAGE, IMAGE);
        intent.putExtra(WEATHER_TEMPERATURE_MIN, TEMPERATURE_MIN);
        intent.putExtra(WEATHER_TEMPERATURE_MAX, TEMPERATURE_MAX);
        intent.putExtra(WEATHER_HUMIDITY, HUMIDITY);
        intent.putExtra(WEATHER_WINDSPEED, WINDSPEED);
        intent.putExtra(WEATHER_CLOUDCOVER, CLOUDCOVER);
        intent.putExtra(WEATHER_PRESSURE, PRESSURE);
        intent.putExtra(WEATHER_SUNRISETIME, SUNRISETIME);
        intent.putExtra(WEATHER_SUNSETTIME, SUNSETTIME);
        intent.putExtra(WEATHER_UPDATE, UPDATE);
        intent.putExtra("weather_flag", String.valueOf(rn));

        if(debug)Log.i(DEBUG_TAG, "WeatherIntentService.sendResult Flag: " + String.valueOf(rn));

        broadcaster = LocalBroadcastManager.getInstance(this);
        broadcaster.sendBroadcast(intent);
    }

}
