package esdip.pickapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {

    private static final String DEBUG_TAG = "DEBUG";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView minus_icon;

    private OnFragmentInteractionListener mListener;

    // Interaction URIs
    public static final String SEE_CURRENT = "SEE_CURRENT";
    public static final Uri MINUS_REQUEST = Uri.parse("fragment_request://esdip.pickapps/" + SEE_CURRENT);

    private ImageView hour1_icon;
    private ImageView day1_icon;

    private TextView weather_hummidity;
    private TextView weather_wind_speed;
    private TextView weather_cloud_cover;
    private TextView weather_pressure;
    private TextView weather_sunrise_time;
    private TextView weather_sunset_time;



    public WeatherForecastFragment() {
        // Required empty public constructor
    }


    public static WeatherForecastFragment newInstance() {
        WeatherForecastFragment fragment = new WeatherForecastFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_forecast, container, false);
    }

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
        if ((WeatherCurrentFragment.hummidity == null) || (WeatherCurrentFragment.hummidity == "")){
            weather_hummidity.setText("");
        }else{
            weather_hummidity.setText("Humedad: " + String.valueOf(Math.round(Double.valueOf(WeatherCurrentFragment.hummidity) * 100)) + "%");
        }

        if ((WeatherCurrentFragment.wind_speed == null) || (WeatherCurrentFragment.wind_speed == "")){
            weather_wind_speed.setText("");
        }else{
            weather_wind_speed.setText("Viento: " + WeatherCurrentFragment.wind_speed);
        }

        if ((WeatherCurrentFragment.cloud_cover == null) || (WeatherCurrentFragment.cloud_cover == "")){
            weather_cloud_cover.setText("");
        }else{
            weather_cloud_cover.setText("Visibilidad: " + WeatherCurrentFragment.cloud_cover);
        }

        if ((WeatherCurrentFragment.pressure == null) || (WeatherCurrentFragment.pressure == "")){
            weather_pressure.setText("");
        }else{
            weather_pressure.setText("Presión: " + WeatherCurrentFragment.pressure);
        }

        /*
        1466052288
        Thu, 16 Jun 2016 04:44:48 GMT
         */
        if ((WeatherCurrentFragment.sunrise_time == null) || (WeatherCurrentFragment.sunrise_time == "")){
            weather_sunrise_time.setText("");
        }else{
            dv = Long.valueOf(WeatherCurrentFragment.sunrise_time)*1000;
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("HH:mm").format(df);
            weather_sunrise_time.setText("Alba: " + vv);
        }

        /* 1466106400
        Thu, 16 Jun 2016 19:46:40 GMT
         */
        if ((WeatherCurrentFragment.sunset_time == null) || (WeatherCurrentFragment.sunset_time == "")){
            weather_sunset_time.setText("");
        }else{
            dv = Long.valueOf(WeatherCurrentFragment.sunset_time)*1000;
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("HH:mm").format(df);
            weather_sunset_time.setText("Ocaso: " + vv);
        }


        Integer icon_size = (int)getActivity().getResources().getDimension(R.dimen.forecast_icon_size);
        Float text_size_normal = getActivity().getResources().getDimension(R.dimen.forecast_large_text_size);
        Float text_size_small = getActivity().getResources().getDimension(R.dimen.forecast_small_text_size);

        Integer right_margin = (int)getActivity().getResources().getDimension(R.dimen.forecast_right_margin);

        minus_icon.setText("-");

        View view = this.getView();

        // Output forecast info grid
        GridLayout gridLayout = (GridLayout)view.findViewById(R.id.grid);

        //gridLayout.removeAllViews();

        int total = 7; // column number

        GridLayout.Spec row0 = GridLayout.spec(0);
        GridLayout.Spec row1 = GridLayout.spec(1);
        GridLayout.Spec row2 = GridLayout.spec(2);
        GridLayout.Spec row3 = GridLayout.spec(3);
        GridLayout.Spec row4 = GridLayout.spec(4);
        GridLayout.Spec row5 = GridLayout.spec(5);
        GridLayout.Spec row6 = GridLayout.spec(6);
        GridLayout.Spec column0 = GridLayout.spec(0);

        //gridLayout.setColumnCount(column);
       // gridLayout.setRowCount(row + 1);



        // Next hours timestamp
        for(int i=0; i<total; i++){

            TextView hour_timestamp = new TextView(getActivity().getBaseContext());
            hour_timestamp.setTextSize(text_size_normal);

            dv = Long.valueOf(WeatherIntentService.next_hours_timestamps.get(i))*1000;
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("HH:mm").format(df); //"MM dd, yyyy hh:mma"
           // Log.i(DEBUG_TAG, "timestamp: " + WeatherIntentService.next_hours_timestamps.get(i) + " hora java: " + vv);
            hour_timestamp.setText(vv);

           // Log.i(DEBUG_TAG, "timestamp: " + WeatherIntentService.next_hours_timestamps.get(i) + " time: " + getDate(Long.parseLong(WeatherIntentService.next_hours_timestamps.get(i))));
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 5;
            param.rowSpec = row0;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);
           // param.setGravity(Gravity.TOP);

            hour_timestamp.setLayoutParams (param);
            gridLayout.addView(hour_timestamp);
        }

        // Next hours icon
        for(int i=0; i<total; i++){

            ImageView hour_icon = new ImageView(getActivity().getBaseContext());
            Bitmap hour_icon_bitmap = BitmapFactory.decodeResource(getContext().getResources(), WeatherIntentService.next_hours_icons.get(i));
            Bitmap hour_icon_resized = Bitmap.createScaledBitmap(hour_icon_bitmap, icon_size, icon_size, false);
            hour_icon.setImageBitmap(hour_icon_resized);

            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 5;
            param.rowSpec = row1;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            hour_icon.setLayoutParams (param);
            gridLayout.addView(hour_icon);
        }

        // Next hours temp
        for(int i=0; i<total; i++){

            TextView hour_temperature = new TextView(getActivity().getBaseContext());
            hour_temperature.setTextSize(text_size_normal);
            hour_temperature.setText(WeatherIntentService.next_hours_temperatures.get(i).substring(0, 2) + "º");

            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 2;
            param.rowSpec = row2;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            hour_temperature.setLayoutParams (param);
            gridLayout.addView(hour_temperature);
        }

        // Days forecast
        // Next days day name
        for(int i=0; i<total; i++){

            TextView day_timestamp = new TextView(getActivity().getBaseContext());
            day_timestamp.setTextSize(text_size_normal);

            dv = Long.valueOf(WeatherIntentService.next_days_timestamps.get(i))*1000;
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("EEE").format(df).toUpperCase(); //https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
            //Log.i(DEBUG_TAG, "timestamp: " + WeatherIntentService.next_hours_timestamps.get(i) + " hora java: " + vv);
            day_timestamp.setText(vv);

            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = (int)getActivity().getResources().getDimension(R.dimen.medium_margin);
            param.rowSpec = row3;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            day_timestamp.setLayoutParams (param);
            gridLayout.addView(day_timestamp);
        }

        // Next days date
        for(int i=0; i<total; i++){

            TextView day_date = new TextView(getActivity().getBaseContext());
            day_date.setTextSize(text_size_small);

            dv = Long.valueOf(WeatherIntentService.next_days_timestamps.get(i))*1000;
            df = new java.util.Date(dv);
            vv = new SimpleDateFormat("dd/MM").format(df); //"MM dd, yyyy hh:mma"
            day_date.setText(vv);

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 3;
            param.rowSpec = row4;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            day_date.setLayoutParams (param);
            gridLayout.addView(day_date);
        }

        // Next days icon
        for(int i=0; i<total; i++){

            ImageView day_icon = new ImageView(getActivity().getBaseContext());
            Bitmap day_icon_bitmap = BitmapFactory.decodeResource(getContext().getResources(), WeatherIntentService.next_days_icons.get(i));
            Bitmap day_icon_resized = Bitmap.createScaledBitmap(day_icon_bitmap, icon_size, icon_size, false);
            day_icon.setImageBitmap(day_icon_resized);

            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 5;
            param.rowSpec = row5;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            day_icon.setLayoutParams (param);
            gridLayout.addView(day_icon);
        }

        // Next days temp
        for(int i=0; i<total; i++){

            TextView days_temp = new TextView(getActivity().getBaseContext());
            days_temp.setTextSize(text_size_normal);
            Float tempMax = Float.parseFloat(WeatherIntentService.next_days_temperatures_max.get(i));
           // Float tempMin = Float.parseFloat(WeatherIntentService.next_days_temperatures_min.get(i));
            //days_temp.setText(String.valueOf(Math.round(tempMax + tempMin) / 2) + "º");
            days_temp.setText(String.valueOf(Math.round(tempMax)) + "º");


            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            param.rightMargin = right_margin;
            param.topMargin = 2;
            param.bottomMargin = 15;
            param.rowSpec = row6;
            param.columnSpec = GridLayout.spec(i);
            param.setGravity(Gravity.CENTER_HORIZONTAL);

            days_temp.setLayoutParams (param);
            gridLayout.addView(days_temp);
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        View view = this.getView();

        minus_icon = (TextView) view.findViewById(R.id.icon_minus);

        minus_icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View minus_icon) {

                mListener.onFragmentInteraction(MINUS_REQUEST);
            }
        });

        weather_hummidity = (TextView) view.findViewById(R.id.txtHum);
        weather_wind_speed = (TextView) view.findViewById(R.id.txtWind);
        weather_cloud_cover = (TextView) view.findViewById(R.id.txtVis);
        weather_pressure = (TextView) view.findViewById(R.id.txtPres);
        weather_sunrise_time = (TextView) view.findViewById(R.id.txtSunrise);
        weather_sunset_time = (TextView) view.findViewById(R.id.txtSunset);


        //hour1_icon = (ImageView) view.findViewById(R.id.hour1_icon);
        //day1_icon = (ImageView) view.findViewById(R.id.day1_icon);

        updateView();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
