package esdip.pickapps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherCurrentFragment extends Fragment {

    private static final String DEBUG_TAG = "DEBUG";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static BroadcastReceiver weather_broadcast_receiver;

    /*public static String latitude;
    public static String longitude;
    public static String city;*/

    //public static String latitude; "//40.25";
    //public static String longitude;//-3.41";
    //public static String city = "Esperando a obtener la localización ...";

    private static String temperature;
    private static String text;
    private static Integer icon_id;

    private static String temperature_min;
    private static String temperature_max;
    public static String hummidity;
    public static String wind_speed;
    public static String cloud_cover;
    public static String pressure;
    public static String sunrise_time;
    public static String sunset_time;
    public static String update_time;

   // private GridLayout grid_layout;
    private TextView more_icon;
    private TextView current_date;
    private ImageView weather_icon;
    private TextView device_location;
    private TextView weather_temperature;
    private TextView weather_description;
    private TextView weather_temperature_min;
    private TextView weather_temperature_max;
    private TextView weather_update_time;
    private TextView weather_update_label;
    //private ImageView update_weather;


    // Interaction URIs
    public static final String SEE_FORECAST = "SEE_FORECAST";
    public static final Uri MORE_REQUEST = Uri.parse("fragment_request://esdip.pickapps/" + SEE_FORECAST);

    public WeatherCurrentFragment() {
        // Required empty public constructor
    }

    public static WeatherCurrentFragment newInstance(String param1, String param2) {
        WeatherCurrentFragment fragment = new WeatherCurrentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        // Request for device location
        //startActionGetLocation(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_current, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Log.i(DEBUG_TAG, "WeatherCurrentFragment onStart");

        // Request for device location
        //startActionGetLocation(getActivity());

        View view = this.getView();

        //grid_layout = (GridLayout)view.findViewById(R.id.grid_layout);
        more_icon = (TextView) view.findViewById(R.id.moreIcon);
        weather_icon = (ImageView) view.findViewById(R.id.imgWeatherIcon);
        current_date = (TextView) view.findViewById(R.id.txtDate);
        device_location = (TextView) view.findViewById(R.id.txtLocation);
        weather_temperature = (TextView) view.findViewById(R.id.txtTemp);
        weather_description = (TextView) view.findViewById(R.id.txtDesc);
        weather_temperature_min = (TextView) view.findViewById(R.id.txtTempMin);
        weather_temperature_max = (TextView) view.findViewById(R.id.txtTempMax);
        weather_update_time = (TextView) view.findViewById(R.id.txtLastUpdate);
        weather_update_label = (TextView)view.findViewById(R.id.txtLastUpdateLabel);
        //update_weather = (ImageView)view.findViewById(R.id.updateDrawer);


        // Set weather_icon click listener
        /*weather_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View weather_icon) {

                Intent weather_intent = new Intent(getActivity().getBaseContext(), WeatherIntentService.class);

                weather_intent.setAction(WeatherIntentService.ACTION_GET_WEATHER_DATA);
                weather_intent.putExtra(LocationIntentService.LOCATION_LATITUDE, LocationIntentService.longitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_LONGITUDE, LocationIntentService.longitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_CITY, LocationIntentService.city);

                getActivity().getBaseContext().startService(weather_intent);
            }
        });*/

        // Set "more" icon
        more_icon.setText("+");
        more_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View more_icon) {

                mListener.onFragmentInteraction(MORE_REQUEST);
            }
        });

        device_location.setText(R.string.location_not_available);


       /* update_weather.setOnClickListener(new View.OnClickListener() {
            public void onClick(View more_icon) {

                // Start MainService
                //getActivity().startService(new Intent(getActivity(), MainService.class));

                Intent weather_intent = new Intent(getActivity(), WeatherIntentService.class);

                weather_intent.setAction(WeatherIntentService.ACTION_GET_WEATHER_DATA);
                weather_intent.putExtra(LocationIntentService.LOCATION_LATITUDE, LocationIntentService.latitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_LONGITUDE, LocationIntentService.longitude);
                weather_intent.putExtra(LocationIntentService.LOCATION_CITY, LocationIntentService.city);

                getActivity().startService(weather_intent);
            }
        });*/

        // Pending intent for weather service
      /*  Intent intent = new Intent(getActivity(), WeatherService.class);
        PendingIntent intentWeatherService = PendingIntent.getService(getActivity(), 0, intent, 0);

        try{
            intentWeatherService.send();

        }catch (PendingIntent.CanceledException ex){
            ex.printStackTrace();
        }*/

        // Timer for retrieving data from weather service
       /* AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 30 * 60 * 1000, intentWeatherService);
*/
        // Broadcaster for weather service result
        weather_broadcast_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                text = intent.getStringExtra(WeatherIntentService.WEATHER_TEXT);
                temperature = intent.getStringExtra(WeatherIntentService.WEATHER_TEMPERATURE);
                icon_id = intent.getIntExtra(WeatherIntentService.WEATHER_ICON, -1);
                temperature_min = intent.getStringExtra(WeatherIntentService.WEATHER_TEMPERATURE_MIN);
                temperature_max = intent.getStringExtra(WeatherIntentService.WEATHER_TEMPERATURE_MAX);
                hummidity = intent.getStringExtra(WeatherIntentService.WEATHER_HUMIDITY);
                wind_speed = intent.getStringExtra(WeatherIntentService.WEATHER_WINDSPEED);
                cloud_cover = intent.getStringExtra(WeatherIntentService.WEATHER_CLOUDCOVER);
                pressure = intent.getStringExtra(WeatherIntentService.WEATHER_PRESSURE);
                sunrise_time = intent.getStringExtra(WeatherIntentService.WEATHER_SUNRISETIME);
                sunset_time = intent.getStringExtra(WeatherIntentService.WEATHER_SUNSETTIME);
                update_time = intent.getStringExtra(WeatherIntentService.WEATHER_UPDATE);

                //Log.i(DEBUG_TAG, "BroadcastReceiver weather text: " + text);

                // Update UI
                updateView();
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(weather_broadcast_receiver, new IntentFilter(WeatherIntentService.WEATHER_FETCHED));

        updateView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updateView(){

        long dv;
        Date df;
        String vv;

        df = new java.util.Date(Calendar.getInstance().getTimeInMillis());
        vv = new SimpleDateFormat("E, d MMM").format(df);

        if ((icon_id == null) || (icon_id < 0)) {
            weather_icon.setBackgroundResource(android.R.drawable.ic_dialog_alert);
            //grid_layout.setBackgroundResource(android.R.drawable.ic_dialog_alert);
        } else {
            //grid_layout.setBackgroundResource(icon_id);
            //weather_icon.setImageResource(icon_id);
            //DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();

            // int dpHeight =  Math.round(displayMetrics.heightPixels / displayMetrics.density);
           // int density = Math.round(displayMetrics.density);
            //int dpHeight =  displayMetrics.heightPixels;
            //int dpWidth = displayMetrics.widthPixels;

            //weather_icon.setMaxWidth(dpWidth/2);
            weather_icon.setBackgroundResource(icon_id);
            weather_icon.setMaxWidth(100);
            weather_icon.setMaxHeight(100);


            more_icon.setVisibility(View.VISIBLE);
        }

        if ((temperature == null) || (temperature == "")){
            weather_temperature.setText("");
        }else{
            weather_temperature.setText(String.valueOf(Math.round(Double.valueOf(temperature))) + "º");
        }

        if ((text == null) || (text == "")){
            weather_description.setText("Obteniendo la información del tiempo ...");
        }else{
            weather_description.setText(text);
        }

        if ((LocationIntentService.city == null) || (LocationIntentService.city == "")){
            device_location.setText("Obteniendo la localización ...");
        }else{
            device_location.setText(LocationIntentService.city);
            current_date.setText(vv);
        }

        if ((temperature_min == null) || (temperature_min == "")){
            weather_temperature_min.setText("");
        }else{
            weather_temperature_min.setText("Min. " + String.valueOf(Math.round(Double.valueOf(temperature_min))) + "º");
        }

        if ((temperature_max == null) || (temperature_max == "")){
            weather_temperature_max.setText("");
        }else{
            weather_temperature_max.setText("Max. " + String.valueOf(Math.round(Double.valueOf(temperature_max))) + "º");
        }

        if ((update_time == null) || (update_time == "")){
            weather_update_time.setText("");
        }else{
            dv = Long.valueOf(update_time);
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("E, d MMM HH:mm").format(df);
            weather_update_time.setText(vv);
            weather_update_label.setText("Última Actualización:");
            //update_weather.setImageResource(R.drawable.update_icon);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


   /* public static void startActionGetLocation(Context context) {
        Log.i(DEBUG_TAG, "Fragment WeatherCurrentFragment.startActionGetLocation");

        Intent intent = new Intent(context, LocationIntentService.class);
        intent.setAction(LocationIntentService.ACTION_GET_LOCATION);
        context.startService(intent);
    }*/

}
