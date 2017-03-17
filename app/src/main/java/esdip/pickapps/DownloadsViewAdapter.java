package esdip.pickapps;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import esdip.pickapps.DownloadsFragment.OnListFragmentInteractionListener;

public class DownloadsViewAdapter extends RecyclerView.Adapter<DownloadsViewAdapter.ViewHolder> {

    private static final String DEBUG_TAG = "DEBUG";

    private final Cursor mValues;
    private final OnListFragmentInteractionListener mListener;
    private final String active_package;
    SharedPreferences settings;
    String clicked_package_name;
    String clicked_package_behavior;
   /* public DownloadsViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }*/

    public DownloadsViewAdapter(Cursor data, String current_package, OnListFragmentInteractionListener listener) {
        mValues = data;
        active_package = current_package;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(view);
    }


    public void createView(final ViewHolder holder, boolean override){

        Log.i(DEBUG_TAG, "DownloadsViewAdapter createView");

    if (mValues != null && mValues.moveToFirst()) {

        while (!mValues.isAfterLast()) {

            if (mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_NAME)) != null || override) {

                String name = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_NAME));
                String behavior = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_BEHAVIOR));

                holder.behavior.setText(behavior);

                if(name.equals(active_package)){
                    //  holder.textActiveView.setText(R.string.current_active_package);
                    byte[] on = mValues.getBlob(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_THUMBNAIL_ON));

                    //String is_downloaded = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_DOWNLOADED));
                    // Log.i(DEBUG_TAG, "name: " + name + " downloaded: " + String.valueOf(is_downloaded));

                    Bitmap package_thumbnail_on = BitmapFactory.decodeByteArray(on, 0, on.length);
                    Log.i(DEBUG_TAG, "Es el paquete activo. Imagen: " + String.valueOf(on.length) );
                    holder.name.setText(name);
                    holder.imageView.setImageBitmap(package_thumbnail_on);
                }else{
                    byte[] off = mValues.getBlob(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_THUMBNAIL_OFF));

                    //String is_downloaded = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_DOWNLOADED));
                    // Log.i(DEBUG_TAG, "name: " + name + " downloaded: " + String.valueOf(is_downloaded));

                    Bitmap package_thumbnail_off = BitmapFactory.decodeByteArray(off, 0, off.length);
                    Log.i(DEBUG_TAG, "No es el paquete activo. Imagen: " + String.valueOf(off.length));
                    holder.name.setText(name);
                    holder.imageView.setImageBitmap(package_thumbnail_off);
                }
            }
            mValues.moveToNext();
        }
}
       /* holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
*/
       holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    clicked_package_name = String.valueOf(holder.name.getText());
                    clicked_package_behavior = String.valueOf(holder.behavior.getText());
                    mListener.onDownloadsFragmentInteraction(clicked_package_name, clicked_package_behavior);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
         createView(holder, false);
       /* if (mValues != null && mValues.moveToFirst()) {
        while (!mValues.isAfterLast()) {

            if (mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_NAME)) != null) {
                String name = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_NAME));

                if (name.equals(active_package)) {
                    //  holder.textActiveView.setText(R.string.current_active_package);
                    byte[] on = mValues.getBlob(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_THUMBNAIL_ON));

                    //String is_downloaded = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_DOWNLOADED));
                    // Log.i(DEBUG_TAG, "name: " + name + " downloaded: " + String.valueOf(is_downloaded));

                    Bitmap package_thumbnail_on = BitmapFactory.decodeByteArray(on, 0, on.length);
                    holder.textView.setText(name);
                    holder.imageView.setImageBitmap(package_thumbnail_on);
                } else {
                    byte[] off = mValues.getBlob(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_THUMBNAIL_OFF));

                    //String is_downloaded = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_WALLPAPER_DOWNLOADED));
                    // Log.i(DEBUG_TAG, "name: " + name + " downloaded: " + String.valueOf(is_downloaded));

                    Bitmap package_thumbnail_off = BitmapFactory.decodeByteArray(off, 0, off.length);
                    holder.textView.setText(name);
                    holder.imageView.setImageBitmap(package_thumbnail_off);
                }
            }
            mValues.moveToNext();
        }
    }
       /* holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
*/
     /*   holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    // mListener.onListFragmentInteraction(holder.mItem);
                    // holder.imageView.setImageBitmap(package_thumbnail_off);
                    createView(holder);
                    String clickedItem = String.valueOf(holder.textView.getText());
                    mListener.onDownloadsFragmentInteraction(clickedItem);
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {

        return mValues.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView name;
        public final TextView behavior;
        public final ImageView imageView;

        public ViewHolder(View view) {

            super(view);
            mView = view;
            name = (TextView) view.findViewById(R.id.package_name);
            behavior = (TextView) view.findViewById(R.id.package_behavior);
            imageView = (ImageView) view.findViewById(R.id.package_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }

    private class activateFeedback extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            mListener.onDownloadsFragmentInteraction(clicked_package_name, clicked_package_behavior);

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Boolean result) {
           // createView(holder, true);
        }

        @Override
        protected void onCancelled() {

        }
    }
}
