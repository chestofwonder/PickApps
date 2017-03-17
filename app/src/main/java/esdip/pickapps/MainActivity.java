package esdip.pickapps;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;


// TODO: en pickapps, si el setting de fondo activo esta a nulo, en updatewallpaper y en ondestroy, volver al fondo por defecto

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, WeatherCurrentFragment.OnFragmentInteractionListener, WeatherForecastFragment.OnFragmentInteractionListener {

    private static final String DEBUG_TAG = "DEBUG";

    //private static final String PROVIDER_NAME = "pickapps.contentprovider";
    //private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/packages");
    //private static final Uri CONTENT_IMAGES_URI = Uri.parse("content://" + PROVIDER_NAME + "/images");

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 0;

    private static AppCompatActivity main_activity;

    public static final int WIDGETS_TAB = 0;
    public static final int WALLPAPERS_SECTION = 1;
    public static final int DOWNLOADS_SECTION = 2;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    SharedPreferences settings;

    DrawerLayout drawer;
    ImageView open_drawer;

    // Fragment interaction
    private FragmentManager fm;
    private FragmentTransaction ft;

    private static final String AUTHORITY = "esdip.pickapps";

    private static final int MORE_REQUEST = 1;
    private static final int MINUS_REQUEST = 2;

    int REQUEST_PLACE_PICKER = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatcher.addURI(AUTHORITY, WeatherCurrentFragment.SEE_FORECAST, MORE_REQUEST);
        uriMatcher.addURI(AUTHORITY, WeatherForecastFragment.SEE_CURRENT, MINUS_REQUEST);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Log.i(DEBUG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main2);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        main_activity = this;

        fm = getSupportFragmentManager();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //open_drawer = (ImageView)findViewById(R.id.openDrawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
              this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void selectCustomLocation(){
        Log.i(DEBUG_TAG, "selectCustomLocation");

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(MainActivity.this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
            Log.i(DEBUG_TAG, "startActivityForResult");
        } catch (GooglePlayServicesRepairableException e) {
            Log.i(DEBUG_TAG, "GooglePlayServicesRepairableException.");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.i(DEBUG_TAG, "GooglePlayServicesNotAvailableException.");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(DEBUG_TAG, "onConnected");
        selectCustomLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(DEBUG_TAG, "onConnectionFailed");
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
            Log.i(DEBUG_TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

   /* public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        Log.i(DEBUG_TAG, "onPickButtonClick");
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
            Log.i(DEBUG_TAG, "startActivityForResult");
        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        Log.i(DEBUG_TAG, "requestCode: " + requestCode);
        Log.i(DEBUG_TAG, "resultCode: " + resultCode);

        if (requestCode == REQUEST_PLACE_PICKER && resultCode == Activity.RESULT_OK) {
            Log.i(DEBUG_TAG, "requestCode");
            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            LatLng place_latlong = place.getLatLng();

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            Log.i(DEBUG_TAG, "latitude: " + String.valueOf(place_latlong.latitude));
            Log.i(DEBUG_TAG, "longitude: " + String.valueOf(place_latlong.longitude));
            Log.i(DEBUG_TAG, "name: " + name);
            Log.i(DEBUG_TAG, "address: " + address);
            Log.i(DEBUG_TAG, "Html.fromHtml(attributions): " + Html.fromHtml(attributions));

            SharedPreferences.Editor edit = settings.edit();
            edit.putString(getString(R.string.custom_latitude), String.valueOf(place_latlong.latitude));
            edit.putString(getString(R.string.custom_longitude), String.valueOf(place_latlong.longitude));
            edit.putString(getString(R.string.custom_city), String.valueOf(name));
            edit.apply();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static void requestLocationPermission(){

        ActivityCompat.requestPermissions(main_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
    }


    public void onFragmentInteraction(Uri uri){

        //Log.i(DEBUG_TAG, String.valueOf(uri));

        switch(uriMatcher.match(uri)){
            case MORE_REQUEST:
                ft = fm.beginTransaction();
                ft.replace(R.id.weather_placeholder, WeatherForecastFragment.newInstance());
                //ft.addToBackStack(null);
                ft.commit();
                break;
            case MINUS_REQUEST:
                ft = fm.beginTransaction();
                ft.replace(R.id.weather_placeholder, WeatherCurrentFragment.newInstance("1", "2"));
                //ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }


    public void goToWidgetContentManager(View view){
        Intent i = new Intent(this, PackageManager.class);
        i.putExtra(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER, WIDGETS_TAB);
        startActivity(i);
    }


    public void goToLiveWallpaperContentManager(View view){
        Intent i = new Intent(this, PackageManager.class);
        i.putExtra(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER, WALLPAPERS_SECTION);
        startActivity(i);
    }


    public void goToDownloadsManager(View view){
        Intent i = new Intent(this, PackageManager.class);
        i.putExtra(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER, DOWNLOADS_SECTION);
        startActivity(i);
    }

    public void goToEditorManager(View view){
        Intent i = new Intent(this, EditorSelectActivity.class);
        //i.putExtra(WidgetsManager.PlaceholderFragment.ARG_SECTION_NUMBER, DOWNLOADS_SECTION);
        startActivity(i);
    }


    protected void onStart() {

        Log.i(DEBUG_TAG, "MainActivity onStart");

        super.onStart();

        // Start MainService
        startService(new Intent(this, MainService.class));

        //Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        //startActivityForResult(Intent.createChooser(intent, "Select Wallpaper"), 1);

        //Intent i = new Intent(Intent.ACTION_SET_WALLPAPER, Uri.parse("content://media/external/images/media/28079"));
        //startActivityForResult(i, 1);
        //android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //content://media/external/images/media/28079
        // mGoogleApiClient.connect();

        // Get active package
        //settings = PreferenceManager.getDefaultSharedPreferences(this);

        // DB Initialization
        //dbManager = new DBManager(this, null, null, 1);

        // Weather widget fragment initialization
        ft = fm.beginTransaction();
        ft.add(R.id.weather_placeholder, WeatherCurrentFragment.newInstance("1", "2"));
        ft.commit();

      /*  try {
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

            int density = Math.round(displayMetrics.density);
            int dpHeight =  displayMetrics.heightPixels;
            int dpWidth = displayMetrics.widthPixels;


            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            Drawable drawable = getResources().getDrawable(R.drawable.atomic_clown_bomb);

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap wallpaper = Bitmap.createScaledBitmap(bitmap, dpWidth, dpHeight, true);
            wallpaperManager.setBitmap(wallpaper);

//Toast.makeText(context, "dpWidth: " + String.valueOf(dpWidth) + "dpHeight: " + String.valueOf(dpHeight), Toast.LENGTH_LONG).show();
// imagen 368 × 668
// fisico 480 x 800
// virtual 730 x 1278

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private Bitmap cropBitmapFromCenterAndScreenSize(Bitmap bitmap) {
        int screenWidth, screenHeight;
        float bitmap_width = bitmap.getWidth(), bitmap_height = bitmap
                .getHeight();
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        Log.i("TAG", "bitmap_width " + bitmap_width);
        Log.i("TAG", "bitmap_height " + bitmap_height);

        float bitmap_ratio = (float) (bitmap_width / bitmap_height);
        float screen_ratio = (float) (screenWidth / screenHeight);
        int bitmapNewWidth, bitmapNewHeight;

        Log.i("TAG", "bitmap_ratio " + bitmap_ratio);
        Log.i("TAG", "screen_ratio " + screen_ratio);

        if (screen_ratio > bitmap_ratio) {
            bitmapNewWidth = (int) screenWidth;
            bitmapNewHeight = (int) (bitmapNewWidth / bitmap_ratio);
        } else {
            bitmapNewHeight = (int) screenHeight;
            bitmapNewWidth = (int) (bitmapNewHeight * bitmap_ratio);
        }

        bitmap = Bitmap.createScaledBitmap(bitmap, bitmapNewWidth,
                bitmapNewHeight, true);

        Log.i("TAG", "screenWidth " + screenWidth);
        Log.i("TAG", "screenHeight " + screenHeight);
        Log.i("TAG", "bitmapNewWidth " + bitmapNewWidth);
        Log.i("TAG", "bitmapNewHeight " + bitmapNewHeight);

        int bitmapGapX, bitmapGapY;
        bitmapGapX = (int) ((bitmapNewWidth - screenWidth) / 2.0f);
        bitmapGapY = (int) ((bitmapNewHeight - screenHeight) / 2.0f);

        Log.i("TAG", "bitmapGapX " + bitmapGapX);
        Log.i("TAG", "bitmapGapY " + bitmapGapY);

        bitmap = Bitmap.createBitmap(bitmap, bitmapGapX, bitmapGapY, screenWidth,screenHeight);
        return bitmap;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();item.getTitle();

        if (id == R.id.nav_custom_localization) {
            Intent i = new Intent(this, LaunchPlacePickerActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_default_localization) {
            SharedPreferences.Editor edit = settings.edit();
            edit.putString(getString(R.string.custom_latitude), null);
            edit.putString(getString(R.string.custom_longitude), null);
            edit.putString(getString(R.string.custom_city), null);
            edit.apply();

            AlertDialog.Builder default_localization_dialog_builder = new AlertDialog.Builder(this);
            default_localization_dialog_builder.setTitle(R.string.default_localization_dialog);
            default_localization_dialog_builder.setPositiveButton(R.string.positive_button, null);
            AlertDialog default_localization_dialog = default_localization_dialog_builder.create();
            default_localization_dialog.show();

        } else if (id == R.id.nav_update) {
            CharSequence[] update_options = {
                    "1 " + getString(R.string.update_settings_dialog_hour),
                    "2 " + getString(R.string.update_settings_dialog_hours),
                    "3 " + getString(R.string.update_settings_dialog_hours),
                    getString(R.string.update_settings_dialog_never)
            };
            AlertDialog.Builder update_settings_dialog_builder = new AlertDialog.Builder(this);
            update_settings_dialog_builder.setTitle(R.string.update_frecuency);
            update_settings_dialog_builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //Log.i(DEBUG_TAG, String.valueOf(id));
                }
            });
            update_settings_dialog_builder.setSingleChoiceItems(update_options, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor edit = settings.edit();
                            edit.putInt(getString(R.string.update_frecuency), id);
                            edit.apply();
                        }
                    });
            AlertDialog update_settings_dialog = update_settings_dialog_builder.create();
            update_settings_dialog.show();

        } else if (id == R.id.nav_esdip) {
            String url = "http://www.esdip.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getBaseContext(), String.valueOf(data.getData()), Toast.LENGTH_LONG).show();
             Log.i(DEBUG_TAG, String.valueOf(data.getData()));
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }*/


    @Override
    protected void onStop() {
       // Log.i(DEBUG_TAG, "onStop");
        super.onStop();
       // WeatherService.updateWallpaper();
       // mGoogleApiClient.disconnect();
    }


    @Override
    protected void onDestroy() {
        Log.i(DEBUG_TAG, "onDestroy");

        super.onDestroy();

        /*try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            wallpaperManager.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //WeatherService.updateWallpaper();
        //mGoogleApiClient.disconnect();
        //stopService(new Intent(this, MainService.class));
    }

}
