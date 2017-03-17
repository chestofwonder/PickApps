package esdip.pickapps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class BehaviorConfigActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WeatherBehaviorFragment.OnFragmentInteractionListener,
        HourBehaviorFragment.OnFragmentInteractionListener,
        DayWeekBehaviorFragment.OnFragmentInteractionListener{

    private static final String DEBUG_TAG = "DEBUG";
    private static final Boolean debug = false;

    private final int SELECT_IMAGE = 1;
    private ImageView image_view;
    private int image_id;

    // Fragment interaction
    private FragmentManager fm;
    private FragmentTransaction ft;

    private WeatherBehaviorFragment weather_fragment;

    private String behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior_config);

        if(debug)Log.i(DEBUG_TAG, "BehaviorConfig Activity onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fm = getSupportFragmentManager();

        Bundle new_bundle = getIntent().getExtras();
        behavior =  new_bundle.getString("behavior", "");

        ft = fm.beginTransaction();

        switch (behavior){
            case "weather":
                weather_fragment = WeatherBehaviorFragment.newInstance();
                ft.replace(R.id.fragment_placeholder, weather_fragment, "weather_fragment_tag");
                break;
            case "day_week":
                ft.replace(R.id.fragment_placeholder, DayWeekBehaviorFragment.newInstance());
                break;
            case "hour":
                ft.replace(R.id.fragment_placeholder, HourBehaviorFragment.newInstance());
                break;
        }

        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(debug)Log.i(DEBUG_TAG, "BehaviorConfig Activity onStart");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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

    public void onWeatherConfigFragmentInteraction(ImageView image_view){

    }

    public void onHourConfigFragmentInteraction(Uri uri){

    }

    public void onDayWeekFragmentInteraction(Uri uri){

    }
}
