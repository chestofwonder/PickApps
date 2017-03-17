package esdip.pickapps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
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

public class CustomWallpapersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CustomWallpapersListFragment.OnListFragmentInteractionListener{

    private static final String DEBUG_TAG = "DEBUG";
    private static final Boolean debug = false;

    private SharedPreferences settings;

    // Fragment interaction
    private FragmentManager fm;
    private FragmentTransaction ft;

    private CustomWallpapersListFragment custom_wallpapers_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_wallpapers);

        if(debug) Log.i(DEBUG_TAG, "BehaviorConfig Activity onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        custom_wallpapers_fragment = CustomWallpapersListFragment.newInstance();
        ft.add(R.id.fragment_placeholder, custom_wallpapers_fragment, "custom_wallpapers_fragmentt_tag");

        ft.commit();
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

    public void onCustomWallpapersListFragmentInteraction(String item, String behavior){

        try{

            SharedPreferences.Editor edit = settings.edit();
            edit.putString(getString(R.string.active_package), item);
            edit.putString(getString(R.string.active_behavior), behavior);
            edit.apply();

            new AlertDialog.Builder(this)
                    .setTitle("Paquete de Fondos Activado")
                    .setMessage("Has activado el paquete de fondos: " + item)
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

            MainService.updateWallpaper();

            this.onResume();

            if(debug)Log.i(DEBUG_TAG, "paquete activado: " + item);

        } catch (Exception ex){
            Log.e(DEBUG_TAG, Log.getStackTraceString(ex));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        custom_wallpapers_fragment = CustomWallpapersListFragment.newInstance();
        ft.replace(R.id.fragment_placeholder, custom_wallpapers_fragment, "custom_wallpapers_fragmentt_tag");

        ft.commit();
    }
}
