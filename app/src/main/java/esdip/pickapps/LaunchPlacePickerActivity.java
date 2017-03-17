package esdip.pickapps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class LaunchPlacePickerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String DEBUG_TAG = "DEBUG";

    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_PLACE_PICKER = 1;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void selectCustomLocation(){
        Log.i(DEBUG_TAG, "selectCustomLocation");

        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(LaunchPlacePickerActivity.this);
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

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

}
