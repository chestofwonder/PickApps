package esdip.pickapps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class DayWeekBehaviorFragment extends Fragment {

    private static final String DEBUG_TAG = "DEBUG";
    private final Boolean debug = false;

    private OnFragmentInteractionListener mListener;
    private GridLayout.LayoutParams layout_param;
    private final int SELECT_IMAGE = 1;
    private final int CROP_RESULT = 2;
    private ImageView clicked_image;
    private HashMap<Bitmap, String> custom_images;

    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final String CUSTOM_WALLPAPERS_PATH = "custom_wallpapers";
    private static final String IMAGES_PATH = "images";
    private static final String CONTENT_URI_CUSTOM_WALLPAPERS = "content://" + PROVIDER_NAME + "/" + CUSTOM_WALLPAPERS_PATH + "/name/%s";
    private static final String CONTENT_URI_IMAGES = "content://" + PROVIDER_NAME + "/" + IMAGES_PATH + "/image_name/%s/package_name/%s";


    public DayWeekBehaviorFragment() {
        // Required empty public constructor
    }

    public static DayWeekBehaviorFragment newInstance() {
        DayWeekBehaviorFragment fragment = new DayWeekBehaviorFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(DEBUG_TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_day_week_behavior, container, false);
        createView(view);

        return view;
    }

    private void createView(View view){

        if(debug)Log.i(DEBUG_TAG, "createView");

        ArrayList<String> week_days = new ArrayList<>();
        week_days.add(getActivity().getResources().getString(R.string.monday));
        week_days.add(getActivity().getResources().getString(R.string.tuesday));
        week_days.add(getActivity().getResources().getString(R.string.wednesday));
        week_days.add(getActivity().getResources().getString(R.string.thursday));
        week_days.add(getActivity().getResources().getString(R.string.friday));
        week_days.add(getActivity().getResources().getString(R.string.saturday));
        week_days.add(getActivity().getResources().getString(R.string.sunday));

        custom_images = new HashMap<>();

        GridLayout grid = (GridLayout)view.findViewById(R.id.grid);

        Float text_size_normal = getActivity().getResources().getDimension(R.dimen.forecast_large_text_size);
        Float text_size_small = getActivity().getResources().getDimension(R.dimen.forecast_small_text_size);
        int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));

        Iterator<String> iterator = week_days.iterator();

        while (iterator.hasNext()){

            String constant = iterator.next();


            // Text field
            layout_param = new GridLayout.LayoutParams();
            layout_param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layout_param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layout_param.topMargin = 10;
            layout_param.setGravity(Gravity.TOP | Gravity.LEFT);

            TextView new_text = new TextView(this.getActivity());
            new_text.setText(constant);
            new_text.setTextSize(text_size_normal);
            new_text.setTextColor(Color.WHITE);
            new_text.setPadding(30, 30, 30, 30);
            new_text.setLayoutParams(layout_param);

            grid.addView(new_text);


            // Image picker field
            layout_param = new GridLayout.LayoutParams();
            layout_param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layout_param.width = GridLayout.LayoutParams.MATCH_PARENT;
            layout_param.topMargin = 10;
            layout_param.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            ImageView new_image = new ImageView(this.getActivity());
            new_image.setPadding(10, 10, 10, 10);
            Bitmap new_image_bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_image_icon);
            Bitmap new_image_resized = Bitmap.createScaledBitmap(new_image_bitmap, icon_size, icon_size, false);
            new_image.setImageBitmap(new_image_resized);
            new_image.setTag(constant);

            new_image_bitmap.recycle();

            grid.addView(new_image);

            new_image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View image) {

                    clicked_image = (ImageView) image;

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_IMAGE);
                }
            });
        }

        layout_param = new GridLayout.LayoutParams();
        layout_param.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layout_param.width = GridLayout.LayoutParams.MATCH_PARENT;
        layout_param.topMargin = 10;
        layout_param.columnSpec = GridLayout.spec(0,2);
        layout_param.setGravity(Gravity.BOTTOM);

        Button accept_button = new Button(this.getActivity());
        accept_button.setText(getActivity().getString(R.string.positive_button));
        accept_button.setTextColor(Color.WHITE);
        accept_button.setLayoutParams(layout_param);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog.Builder save_wallpaper_dialog_builder = new AlertDialog.Builder(getContext());
                save_wallpaper_dialog_builder.setTitle(R.string.wallpaper_name);
                save_wallpaper_dialog_builder.setView(input);
                save_wallpaper_dialog_builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Insert new wallpaper into db
                        saveCustomWallpaper(String.valueOf(input.getText()));

                        // Start custom wallpapers activity in order to activate the new wallpaper
                        Intent i = new Intent(getActivity(), CustomWallpapersActivity.class);
                        startActivity(i);
                    }
                });
                AlertDialog update_settings_dialog = save_wallpaper_dialog_builder.create();
                update_settings_dialog.show();
            }
        });
        grid.addView(accept_button);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDayWeekFragmentInteraction(uri);
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

        void onDayWeekFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();

            if(uri != null) {

                final Bitmap selectedImage = decodeUriToBitmap(data.getData());

                int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
                Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

                clicked_image.setImageBitmap(new_image_resized);

                DisplayMetrics displayMetrics = getActivity().getBaseContext().getResources().getDisplayMetrics();

                int device_width = displayMetrics.widthPixels;
                int device_height =  displayMetrics.heightPixels;
                int aspectX = 1;
                int aspectY = 1;
                double ratio = Double.valueOf(device_height) / Double.valueOf(device_width);

                if(debug)Log.i(DEBUG_TAG, "device_height: " + String.valueOf(device_height));
                if(debug)Log.i(DEBUG_TAG, "device_width: " + String.valueOf(device_width));
                if(debug)Log.i(DEBUG_TAG, "ratio: " + String.valueOf(ratio));
                if(debug)Log.i(DEBUG_TAG, "ratio format: " + String.format("%.2f", ratio));

                switch(String.format("%.2f", ratio)){
                    case "1,67":
                        aspectX = 3;
                        aspectY = 5;
                        break;
                    case "1,78":
                        aspectX = 9;
                        aspectY = 16;
                        break;
                    case "0,75":
                        aspectX = 4;
                        aspectY = 3;
                        break;
                    case "1,6":
                        aspectX = 5;
                        aspectY = 8;
                        break;
                    case "0,62":
                        aspectX = 8;
                        aspectY = 5;
                        break;
                }

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(data.getData(), "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", aspectX);
                intent.putExtra("aspectY", aspectY);
                intent.putExtra("outputX", device_width);
                intent.putExtra("outputY", device_height);

                // TODO: Environment.getExternalStorageDirectory(), "/temp/temporary_holder.jpg");
                File f = new File(Environment.getExternalStorageDirectory(), "/temporary_holder.jpg");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                uri = Uri.fromFile(f);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(intent, CROP_RESULT);
            }
        }else if (requestCode == CROP_RESULT && resultCode == Activity.RESULT_OK) {


            String filePath = Environment.getExternalStorageDirectory() + "/temporary_holder.jpg";

            Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

            if(debug)Log.i(DEBUG_TAG, "outputX: " + String.valueOf(imageBitmap.getWidth()));
            if(debug)Log.i(DEBUG_TAG, "outputY: " + String.valueOf(imageBitmap.getHeight()));

            custom_images.put(imageBitmap, clicked_image.getTag().toString());

            File file = new File(filePath);
            file.delete();
        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    private Bitmap decodeUriToBitmap (Uri uri){

        try {
            final InputStream imageStream = getActivity().getBaseContext().getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void saveCustomWallpaper(String package_name) {

        final String new_package = package_name;

        // Save wallpaper
        try{

            ContentValues contentValues = new ContentValues();

            contentValues.put("wallpaper_name", new_package);
            contentValues.put("wallpaper_behavior", MainService.BEHAVIOR_DAY_WEEK);

            Uri uri = getActivity().getContentResolver().insert(Uri.parse(String.format(CONTENT_URI_CUSTOM_WALLPAPERS, new_package)), contentValues);

            if(debug)Log.i(DEBUG_TAG, " custom wallpaper insertado: " + new_package);

            // Save images
            Iterator it = custom_images.entrySet().iterator();

            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();

                if(debug)Log.i(DEBUG_TAG, String.valueOf(pair.getValue()));

                installImage((Bitmap)pair.getKey(), String.valueOf(pair.getValue()), new_package);

                it.remove(); // avoids a ConcurrentModificationException
            }

        } catch(Exception ex){
            Log.e(DEBUG_TAG, Log.getStackTraceString(ex));
        }
    }


    private void installImage(Bitmap bitmap, String image_name, String package_name) {

        if (debug) Log.i(DEBUG_TAG, "WeatherBehaviorFragment.installImage bitmap: " + String.valueOf(bitmap.toString() + " image_name: " + image_name + " package_name " + package_name));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);

        ContentValues contentValues = new ContentValues();

        contentValues.put("package_name", package_name);
        contentValues.put("image_name", image_name);
        contentValues.put("image_data", stream.toByteArray());

        getActivity().getContentResolver().insert(Uri.parse(String.format(CONTENT_URI_IMAGES, image_name, package_name)), contentValues);

        if (debug) Log.i(DEBUG_TAG, "imagen insertada");
    }

}
