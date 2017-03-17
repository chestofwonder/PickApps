package esdip.pickapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

public class EditorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,  GoogleApiClient.OnConnectionFailedListener{

    private static final String DEBUG_TAG = "DEBUG";
    private GoogleApiClient mGoogleApiClient;
    int REQUEST_PLACE_PICKER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.i(DEBUG_TAG, "onNavigationItemSelected");
        // Handle navigation view item clicks here.
        int id = item.getItemId();item.getTitle();

        if (id == R.id.nav_custom_localization) {

        } else if (id == R.id.nav_default_localization) {

        } else if (id == R.id.nav_update) {

        } else if (id == R.id.nav_esdip) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(DEBUG_TAG, "onConnected");
        Log.i(DEBUG_TAG, "onPickButtonClick");
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(EditorActivity.this);
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

}
