package esdip.pickapps;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

public class NewWallpaperConfigActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String DEBUG_TAG = "DEBUG";
    private static final String WALLPAPER_BEHAVIOR = "WALLPAPER_BEHAVIOR";
    private int behavior;
    private Intent new_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallpaper_config);

        behavior = R.id.weather;

        new_intent = new Intent(this, BehaviorConfigActivity.class);
        new_intent.putExtra("behavior", "weather");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.weather:
                if (checked)
                    behavior = R.id.weather;
                    new_intent.putExtra("behavior", "weather");
                    break;
            case R.id.day_week:
                if (checked)
                    behavior = R.id.day_week;
                    new_intent.putExtra("behavior", "day_week");
                    break;
            case R.id.hour:
                if (checked)
                    behavior = R.id.hour;
                    new_intent.putExtra("behavior", "hour");
                    break;
        }
    }

    public void onWallpaperConfigAccept(View view) {
        startActivity(new_intent);
    }
}
