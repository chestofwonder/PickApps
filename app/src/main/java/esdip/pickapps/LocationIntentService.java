package esdip.pickapps;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class LocationIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String DEBUG_TAG = "DEBUG";
    private static boolean debug = false;

    public static final String ACTION_GET_LOCATION = "esdip.pickapps.action.get_location";

    public static final String LOCATION_FETCHED = "esdip.pickapps.LOCATION_FETCHED";
    public static final String LOCATION_LATITUDE = "esdip.pickapps.LOCATION_LATITUDE";
    public static final String LOCATION_LONGITUDE = "esdip.pickapps.LOCATION_LONGITUDE";
    public static final String LOCATION_CITY = "esdip.pickapps.LOCATION_CITY";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    //private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static String latitude;
    public static String longitude;
    public static String city;

    private static String current_location;

    private static LocalBroadcastManager broadcaster;

    private static SharedPreferences settings;

    private static Intent result_intent;

    public LocationIntentService() {
        super("LocationIntentService");
    }



    @Override
    public void onCreate() {
        super.onCreate();

        if(debug) Log.i(DEBUG_TAG, "LocationIntentService onCreate");

        // Initialize Google API client
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(debug) Log.i(DEBUG_TAG, "LocationIntentService onHandleIntent");

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LOCATION.equals(action)) {
                handleActionGetLocation();
            }
        }
    }

    private void handleActionGetLocation() {

        if(debug)Log.i(DEBUG_TAG, "LocationIntentService handleActionGetLocation");

        if( null != settings.getString(getString(R.string.custom_latitude), null ) &&
                null != settings.getString(getString(R.string.custom_longitude), null ) &&
                null != settings.getString(getString(R.string.custom_city), null) ){

            latitude = settings.getString(getString(R.string.custom_latitude), null);
            longitude = settings.getString(getString(R.string.custom_longitude), null);
            city = settings.getString(getString(R.string.custom_city), null);
        } else {
            // Connect to Google API
            mGoogleApiClient.connect();
        }

        sendResult();
    }



    private void getLocation(){

        if(debug)Log.i(DEBUG_TAG, "LocationIntentService.getLocation");

        if ( ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            //Toast.makeText(getBaseContext(), "Por favor, activa el GPS", Toast.LENGTH_LONG).show();
           // ActivityCompat.requestPermissions(getBaseContext(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            MainActivity.requestLocationPermission();

        }else{

            city = getString(R.string.location_not_available);
            //device_location.setText(city);

            Location location;

            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                if(debug)Log.i(DEBUG_TAG, "Esperando a obtener la localización ...");

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                // There is no last know location in google play
                // http://stackoverflow.com/questions/2938502/sending-post-data-in-android
                // https://developers.google.com/maps/documentation/geolocation/intro?hl=es#requests
// TODO: localizacion: http://stackoverflow.com/questions/22386497/filenotfoundexception-when-trying-to-find-nearby-places-with-maps-api
                if (location == null) {
                    if(debug)Log.i(DEBUG_TAG, "No hay localizacion de momento");
                   /* try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        URL url = new URL("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyD0ivsM_Scl4l6bkHr2rlbLJZvkTgS7F80");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        StringBuffer json = new StringBuffer(1024);
                        String tmp = "";
                        while ((tmp = reader.readLine()) != null)
                            json.append(tmp).append("\n");
                        reader.close();

                        Log.i(DEBUG_TAG, "longitud del json api de google: " + String.valueOf(json.length()));

                        JSONObject data = new JSONObject(json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(DEBUG_TAG, "No hay localizacion de ninguna manera");*/
                }

            } else {
                // Set found location
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                // Get city
                try {
                    String latlng = latitude + "," + longitude;
                    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlng);

                    if(debug)Log.i(DEBUG_TAG, "llamada a localización: " + String.valueOf(url));

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    if(debug) Log.i(DEBUG_TAG, "longitud del json: " + String.valueOf(json.length()));

                    JSONObject data = new JSONObject(json.toString());
                    JSONArray json_results = data.getJSONArray("results");
                    JSONObject json_result = json_results.getJSONObject(0);
                    JSONArray json_address_components = json_result.getJSONArray("address_components");

                    for(int i = 0; i < json_address_components.length(); i++){
                        JSONObject json_address_component = json_address_components.getJSONObject(i);
                        String types = json_address_component.getString("types");
                        if(types.contains("locality")){
                            if(debug)Log.i(DEBUG_TAG, json_address_component.getString("long_name"));
                            city =  json_address_component.getString("long_name");
                        }
                    }

                    // Set device location
                    //device_location.setText(city);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //latitude = "40.25";
            //longitude = "-3.41";
            //city = "Madrid";

            sendResult();
        }
    }


    static void sendResult() {

        if(debug)Log.i(DEBUG_TAG, "LocationIntentService.sendResult: " + city);

      //  current_location = settings.getString("current_location", "" );

        if(null != city && "" != city && "Obteniendo la localización ..." != city) {

            Random rn = new Random();
            Log.i(DEBUG_TAG, String.valueOf(rn));

            SharedPreferences.Editor edit = settings.edit();
            edit.putString("location_flag", "");
            edit.apply();

            result_intent = new Intent(LOCATION_FETCHED);
            result_intent.putExtra(LOCATION_LATITUDE, latitude);
            result_intent.putExtra(LOCATION_LONGITUDE, longitude);
            result_intent.putExtra(LOCATION_CITY, city);
            result_intent.putExtra("location_flag", String.valueOf(rn));

          /*  if (current_location.equals(city)) {
                Log.i(DEBUG_TAG, "current_location igual a city");
            }else{
                SharedPreferences.Editor edit = settings.edit();
                edit.putString("current_location", city);
                edit.apply();

                Log.i(DEBUG_TAG, "current_location: " + settings.getString("current_location", ""));
                Log.i(DEBUG_TAG, "city: " + city);
                Log.i(DEBUG_TAG, "LocationIntentService.sendResult:  broadcaster.sendBroadcast(result_intent);");
*/
                broadcaster.sendBroadcast(result_intent);
           // }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if(debug)Log.i(DEBUG_TAG, "LocationIntentService: onConnected");

        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
          //  try {
                // Start an Activity that tries to resolve the error
               // connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
           // } catch (IntentSender.SendIntentException e) {
                // Log the error
            //    e.printStackTrace();
            //}
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            if (connectionResult.getErrorCode() == 1){
                // Google Play is not present on device
                Toast.makeText(getBaseContext(), "Google Play no está disponible en el dispositivo", Toast.LENGTH_SHORT).show();
            }
            if(debug)Log.i(DEBUG_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if(debug)Log.i(DEBUG_TAG, "onLocationChanged");

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
    }

    @Override
    public void onDestroy() {
        if(debug)Log.i(DEBUG_TAG, "LocationIntentService.onDestroy");
        super.onDestroy();
    }
}
