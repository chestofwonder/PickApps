package esdip.pickapps;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


// TODO: en los crop result de cada grupo, meter la imagen en los imageview correspondientes
// TODO: ver por qué te vuelve a sacar la última imagen que has seleccionado

public class HourBehaviorFragment extends Fragment {

    private static final String DEBUG_TAG = "DEBUG";
    private final Boolean debug = false;

    private OnFragmentInteractionListener mListener;
    private GridLayout.LayoutParams layout_param;
    private final int SELECT_IMAGE = 1;
    private final int SELECT_IMAGE_MOURNING = 2;
    private final int SELECT_IMAGE_AFTERNOON = 3;
    private final int SELECT_IMAGE_EVENING  = 4;
    private final int SELECT_IMAGE_NIGHT = 5;
    private final int CROP_RESULT = 6;
    private final int CROP_RESULT_MOURNING = 7;
    private final int CROP_RESULT_AFTERNOON = 8;
    private final int CROP_RESULT_EVENING = 9;
    private final int CROP_RESULT_NIGHT = 10;
    private ImageView clicked_image;
    private HashMap<Bitmap, String> custom_images;

    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final String CUSTOM_WALLPAPERS_PATH = "custom_wallpapers";
    private static final String IMAGES_PATH = "images";
    private static final String CONTENT_URI_CUSTOM_WALLPAPERS = "content://" + PROVIDER_NAME + "/" + CUSTOM_WALLPAPERS_PATH + "/name/%s";
    private static final String CONTENT_URI_IMAGES = "content://" + PROVIDER_NAME + "/" + IMAGES_PATH + "/image_name/%s/package_name/%s";


    public HourBehaviorFragment() {
        // Required empty public constructor
    }

    public static HourBehaviorFragment newInstance() {
        HourBehaviorFragment fragment = new HourBehaviorFragment();

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

        View view = inflater.inflate(R.layout.fragment_hour_behavior, container, false);
        createView(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

       if (requestCode == SELECT_IMAGE && resultCode == Activity.RESULT_OK) {

           Uri uri = data.getData();

           if (uri != null) {

               final Bitmap selectedImage = decodeUriToBitmap(data.getData());

               int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
               Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

               clicked_image.setImageBitmap(new_image_resized);

               getCroppedImage(data, "");
           }

       } else if (requestCode == SELECT_IMAGE_MOURNING && resultCode == Activity.RESULT_OK) {

           Uri uri = data.getData();

           if (uri != null) {

               final Bitmap selectedImage = decodeUriToBitmap(data.getData());

               int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
               Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

               clicked_image.setImageBitmap(new_image_resized);

               ImageView image_view;
               image_view = (ImageView)getView().findViewById(R.id.hour_7_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_8_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_9_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_10_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_11_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_12_image);
               image_view.setImageBitmap(new_image_resized);


               getCroppedImage(data, getString(R.string.mourning));
           }

       } else if (requestCode == SELECT_IMAGE_AFTERNOON && resultCode == Activity.RESULT_OK) {

           Uri uri = data.getData();

           if (uri != null) {

               final Bitmap selectedImage = decodeUriToBitmap(data.getData());

               int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
               Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

               clicked_image.setImageBitmap(new_image_resized);

               ImageView image_view;
               image_view = (ImageView)getView().findViewById(R.id.hour_13_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_14_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_15_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_16_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_17_image);
               image_view.setImageBitmap(new_image_resized);
               image_view = (ImageView)getView().findViewById(R.id.hour_18_image);
               image_view.setImageBitmap(new_image_resized);

               getCroppedImage(data, getString(R.string.afternoon));
           }

       } else if (requestCode == SELECT_IMAGE_EVENING && resultCode == Activity.RESULT_OK) {

           Uri uri = data.getData();

           if (uri != null) {

               final Bitmap selectedImage = decodeUriToBitmap(data.getData());

               int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
               Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

               clicked_image.setImageBitmap(new_image_resized);

               getCroppedImage(data, getString(R.string.evening));
           }

       } else if (requestCode == SELECT_IMAGE_NIGHT && resultCode == Activity.RESULT_OK) {

           Uri uri = data.getData();

           if (uri != null) {

               final Bitmap selectedImage = decodeUriToBitmap(data.getData());

               int icon_size = Math.round(getActivity().getResources().getDimension(R.dimen.config_icon_size));
               Bitmap new_image_resized = Bitmap.createScaledBitmap(selectedImage, icon_size, icon_size, false);

               clicked_image.setImageBitmap(new_image_resized);

               getCroppedImage(data, getString(R.string.night));
           }

       } else if (requestCode == CROP_RESULT && resultCode == Activity.RESULT_OK) {

            String filePath = Environment.getExternalStorageDirectory() + "/temporary_holder.jpg";

            Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

            if(debug)Log.i(DEBUG_TAG, "outputX: " + String.valueOf(imageBitmap.getWidth()));
            if(debug)Log.i(DEBUG_TAG, "outputY: " + String.valueOf(imageBitmap.getHeight()));

            custom_images.put(imageBitmap, clicked_image.getTag().toString());

            File file = new File(filePath);
            file.delete();

        } else if (requestCode == CROP_RESULT_MOURNING && resultCode == Activity.RESULT_OK) {

           String filePath = Environment.getExternalStorageDirectory() + "/temporary_holder.jpg";

           Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

           if(debug)Log.i(DEBUG_TAG, "outputX: " + String.valueOf(imageBitmap.getWidth()));
           if(debug)Log.i(DEBUG_TAG, "outputY: " + String.valueOf(imageBitmap.getHeight()));

           custom_images.put(imageBitmap, clicked_image.getTag().toString());

           File file = new File(filePath);
           file.delete();

        } else if (requestCode == CROP_RESULT_AFTERNOON && resultCode == Activity.RESULT_OK) {

           String filePath = Environment.getExternalStorageDirectory() + "/temporary_holder.jpg";

           Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

           if(debug)Log.i(DEBUG_TAG, "outputX: " + String.valueOf(imageBitmap.getWidth()));
           if(debug)Log.i(DEBUG_TAG, "outputY: " + String.valueOf(imageBitmap.getHeight()));

           custom_images.put(imageBitmap, "13");
           custom_images.put(imageBitmap, "14");
           custom_images.put(imageBitmap, "15");
           custom_images.put(imageBitmap, "16");
           custom_images.put(imageBitmap, "17");
           custom_images.put(imageBitmap, "18");

           File file = new File(filePath);
           file.delete();

        } else if (requestCode == CROP_RESULT_EVENING && resultCode == Activity.RESULT_OK) {

           String filePath = Environment.getExternalStorageDirectory() + "/temporary_holder.jpg";

           Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

           if(debug)Log.i(DEBUG_TAG, "outputX: " + String.valueOf(imageBitmap.getWidth()));
           if(debug)Log.i(DEBUG_TAG, "outputY: " + String.valueOf(imageBitmap.getHeight()));

           custom_images.put(imageBitmap, clicked_image.getTag().toString());

           File file = new File(filePath);
           file.delete();

        } else if (requestCode == CROP_RESULT_NIGHT && resultCode == Activity.RESULT_OK) {

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


    private void getCroppedImage(Intent data, String tag){
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

        Uri uri = Uri.fromFile(f);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        if(tag.equals("")){
            startActivityForResult(intent, CROP_RESULT);
        } else if(tag.equals(getString(R.string.mourning))){
            startActivityForResult(intent, CROP_RESULT_MOURNING);
        } else if(tag.equals(getString(R.string.afternoon))){
            startActivityForResult(intent, CROP_RESULT_AFTERNOON);
        } else if(tag.equals(getString(R.string.evening))){
            startActivityForResult(intent, CROP_RESULT_EVENING);
        } else if(tag.equals(getString(R.string.night))){
            startActivityForResult(intent, CROP_RESULT_NIGHT);
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


    private void createView(View view){

        if(debug)Log.i(DEBUG_TAG, "createView");

        custom_images = new HashMap<>();

        GridLayout grid = (GridLayout)view.findViewById(R.id.grid);

        // MOURNING
        TextView mourning_textview = (TextView) view.findViewById(R.id.morning_group);
        mourning_textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleHours(v);
            }
        });


        // AFTERNOON
        TextView afternoon_textview = (TextView) view.findViewById(R.id.afternoon_group);
        afternoon_textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleHours(v);
            }
        });


        // EVENING
        TextView evening_textview = (TextView) view.findViewById(R.id.evening_group);
        evening_textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleHours(v);
            }
        });


        // NIGHT
        TextView night_textview = (TextView) view.findViewById(R.id.night_group);
        night_textview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggleHours(v);
            }
        });


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
            mListener.onHourConfigFragmentInteraction(uri);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<View> views = new ArrayList<View>();
        views = createImageViewListeners((ViewGroup) this.getView(), android.support.v7.widget.AppCompatImageView.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onHourConfigFragmentInteraction(Uri uri);
    }

    private void saveCustomWallpaper(String package_name) {

        final String new_package = package_name;

        // Save wallpaper
        try{

            ContentValues contentValues = new ContentValues();

            contentValues.put("wallpaper_name", new_package);
            contentValues.put("wallpaper_behavior", MainService.BEHAVIOR_HOUR);

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

    // Method to manage elements visibility
    public void toggleHours(View v){

        String view_tag = String.valueOf(v.getTag());
        TextView text_view;
        ImageView image_view;

        if (view_tag.equals(getString(R.string.mourning))){

            text_view = (TextView)this.getView().findViewById(R.id.hour_7_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_7_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_8_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_8_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_9_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_9_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_10_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_10_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_11_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_11_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_12_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_12_image);
            toggleVisibility(image_view);

        } else if (view_tag.equals(getString(R.string.afternoon))){

            text_view = (TextView)this.getView().findViewById(R.id.hour_13_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_13_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_14_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_14_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_15_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_15_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_16_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_16_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_17_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_17_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_18_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_18_image);
            toggleVisibility(image_view);

        } else if (view_tag.equals(getString(R.string.evening))){

            text_view = (TextView)this.getView().findViewById(R.id.hour_19_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_19_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_20_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_20_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_21_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_21_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_22_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_22_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_23_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_23_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_24_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_24_image);
            toggleVisibility(image_view);

        } else if (view_tag.equals(getString(R.string.night))){

            text_view = (TextView)this.getView().findViewById(R.id.hour_1_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_1_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_2_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_2_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_3_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_3_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_4_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_4_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_5_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_5_image);
            toggleVisibility(image_view);

            text_view = (TextView)this.getView().findViewById(R.id.hour_6_text);
            toggleVisibility(text_view);
            image_view = (ImageView)this.getView().findViewById(R.id.hour_6_image);
            toggleVisibility(image_view);

        }

        // no sirve de aqui hacia abajo

        /*ArrayList<View> views;

        views = getViewsByTag((ViewGroup)this.getView(), String.valueOf(v.getTag()));

        Iterator it = views.iterator();

        while (it.hasNext()) {

            View current_view = (View)it.next();

            toggleVisibility(current_view);

            it.remove(); // avoids a ConcurrentModificationException
        }*/

    }

    private  ArrayList<View> createImageViewListeners(ViewGroup root, Class view_class){
      //  class android.support.v7.widget.AppCompatImageView
        ArrayList<View> views = new ArrayList<View>();

        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);

            if (child instanceof ViewGroup) {
                views.addAll(createImageViewListeners((ViewGroup) child, view_class));
            }

            if (child.getClass().equals(view_class)) {

                child.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View image) {

                        clicked_image = (ImageView) image;

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                        intent.setType("image/*");

                        if(image.getTag().equals(getString(R.string.mourning))){
                            startActivityForResult(intent, SELECT_IMAGE_MOURNING);
                        } else if (image.getTag().equals(getString(R.string.afternoon))){
                            startActivityForResult(intent, SELECT_IMAGE_AFTERNOON);
                        } else if (image.getTag().equals(getString(R.string.evening))){
                            startActivityForResult(intent, SELECT_IMAGE_EVENING);
                        } else if (image.getTag().equals(getString(R.string.night))){
                            startActivityForResult(intent, SELECT_IMAGE_NIGHT);
                        } else {
                            startActivityForResult(intent, SELECT_IMAGE);
                        }
                    }
                });
            }
        }

        return views;
    }

    // Get all views by tag
    private  ArrayList<View> getViewsByTag(ViewGroup root, String tag){

        ArrayList<View> views = new ArrayList<View>();

        final int childCount = root.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }

        return views;
    }

    private void toggleVisibility(View v) {

        if (v.getVisibility() == View.GONE) {
            v.setVisibility(View.VISIBLE);
        } else if (v.getVisibility() == View.VISIBLE){
            v.setVisibility(View.GONE);
        }
    }

}


