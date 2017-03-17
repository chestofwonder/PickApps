package esdip.pickapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class borrar_ContentViewsAdapter extends ArrayAdapter<String> {

    private static final String DEBUG_TAG = "DEBUG";
    private final Context context;
    private final Cursor data;
    SharedPreferences settings;

    public borrar_ContentViewsAdapter(Context context, Cursor data) {

        super(context, R.layout.row_layout);

        Log.i(DEBUG_TAG, "borrar_ContentViewsAdapter constructor");

        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i(DEBUG_TAG, "borrar_ContentViewsAdapter getView");

        settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        String active_package = settings.getString(getContext().getString(R.string.active_package), null);
        Log.i(DEBUG_TAG, "active package: " + active_package);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_layout, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.package_name);
       // TextView textActiveView = (TextView) rowView.findViewById(R.id.package_active);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.package_thumbnail);

        data.moveToFirst();

        while (!data.isAfterLast()) {

            if (data.getString(data.getColumnIndex("package_name")) != null) {
                String name = data.getString(data.getColumnIndex(DBManager.COLUMN_WALLPAPER_NAME));
               // byte[] thumbnail = data.getBlob(data.getColumnIndex(DBManager.COLUMN_WALLPAPER_THUMBNAIL));
                //Bitmap package_thumbnail = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
                textView.setText(name);
                //imageView.setImageBitmap(package_thumbnail);

               /* if(name.equals(active_package)){
                    textActiveView.setText(getContext().getString(R.string.current_active_package));
                }else{
                    textActiveView.setText("");
                }*/
            }
            data.moveToNext();
        }

        return rowView;
    }

    @Override
    public int getCount() {
        return data.getCount();
    }
}
