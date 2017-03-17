package esdip.pickapps;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import esdip.pickapps.packages.wallpapers;
import esdip.pickapps.packages.widgets;

public class PackageManager extends AppCompatActivity implements
        WallpapersFragment.OnListFragmentInteractionListener,
        DownloadsFragment.OnListFragmentInteractionListener,
        WidgetsFragment.OnListFragmentInteractionListener{

    private static final String DEBUG_TAG = "DEBUG";
    private static boolean debug = false;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DrawerLayout drawer;
    private SharedPreferences settings;
    private int tab_saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widgets_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

       /* TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab;

        int selected_section = 1;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            selected_section = extras.getInt(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER);
            Log.i(DEBUG_TAG, "seccion: " + selected_section);
        }

        tab = tabLayout.getTabAt(selected_section);

        tab.select();*/
    }


    @Override
    protected void onStart() {
        super.onStart();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab;

        int selected_section = 1;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            selected_section = extras.getInt(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER);
            Log.i(DEBUG_TAG, "nombre seccion: " + PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER);
        }

        tab = tabLayout.getTabAt(selected_section);

        tab.select();
    }

    public void onWallpapersFragmentInteraction(wallpapers.wallpaper item){
        if(debug)Log.i(DEBUG_TAG, "onWallpapersFragmentInteraction");

        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.buy_link));
        startActivity(i);
    }


    public void onDownloadsFragmentInteraction(String item, String behavior){
        // Get clicked package and set it as the active one
        //TextView clickedView = (TextView)l.findViewById(R.id.package_name);

        SharedPreferences.Editor edit = settings.edit();
        edit.putString(getString(R.string.active_package), item);
        edit.putString(getString(R.string.active_behavior), behavior);
        edit.apply();
// TODO: No activar si ya era el paquete activo
        new AlertDialog.Builder(this)
                .setTitle("Paquete de Fondos Activado")
                .setMessage("Has activado el paquete de fondos: " + item)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();

        MainService.updateWallpaper();


       /* FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.weather_placeholder, DownloadsFragment.newInstance());
        tr.commit()*/
        this.onResume();

        if(debug)Log.i(DEBUG_TAG, "paquete activado: " + item);
    }

    public void onWidgetsFragmentInteraction(widgets.widget item){

        if(debug)Log.i(DEBUG_TAG, "onWidgetsFragmentInteraction");

        Intent i = new Intent(android.content.Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.buy_link));
        startActivity(i);
    }

    public static class PlaceholderFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = null;
            ImageView imageView;

           /* switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_widgets_manager, container, false);
                    imageView = (ImageView)rootView.findViewById(R.id.blackboard_weather);
                    imageView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {
                                                         Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                                                         i.setData(Uri.parse("https://play.google.com/store/apps/details?id=my packagename "));
                                                         startActivity(i);
                                                     }
                                                 }
                    );
                    //textView = (TextView) rootView.findViewById(R.id.section_label);
                    //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                    //textView.setText("seccion widgets");
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.fragment_wallpapers_manager, container, false);
                   // textView = (TextView) rootView.findViewById(R.id.section_label);
                    //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                   // textView.setText("seccion fondos");
                    break;
                case 3:
                    //rootView = inflater.inflate(R.layout.activity_live_wallpaper_content_manager, container, false);
                    //rootView = inflater.inflate(R.layout.fragment_widgets_manager, container, false);
                    //textView = (TextView) rootView.findViewById(R.id.section_label);
                    //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                    //textView.setText("seccion descargas");
                    break;
            }*/

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            tab_saved = position;

            switch(position){
                case 0:
                    return WidgetsFragment.newInstance();
                case 1:
                    return WallpapersFragment.newInstance();
                case 2:
                    return DownloadsFragment.newInstance();
                default:
                    return null;
            }

            //WallpapersFragment wallpapers = WallpapersFragment.newInstance("1", "2");
            //return wallpapers;
           // return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.section_widgets);
                case 1:
                    return getResources().getString(R.string.section_wallpapers);
                case 2:
                    return getResources().getString(R.string.section_installed);
            }
            return null;
        }


    }

    @Override
    protected void onDestroy() {

        if(debug)Log.i(DEBUG_TAG, "PackageManager onDestroy");

        super.onDestroy();
    }

    @Override
    protected void onStop() {

        if(debug)Log.i(DEBUG_TAG, "PackageManager onStop");

        super.onStop();
    }

    @Override
    protected void onPause() {

        if(debug)Log.i(DEBUG_TAG, "PackageManager onPause");

        super.onPause();
    }

    @Override
    protected void onResume() {

        if(debug)Log.i(DEBUG_TAG, "PackageManager onResume");

        super.onResume();

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TabLayout.Tab tab;

        int selected_section = 1;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selected_section = extras.getInt(PackageManager.PlaceholderFragment.ARG_SECTION_NUMBER);
        }

        tab = tabLayout.getTabAt(selected_section);

        tab.select();
    }
}
