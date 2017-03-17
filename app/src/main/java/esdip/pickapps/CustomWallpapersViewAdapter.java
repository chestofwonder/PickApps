package esdip.pickapps;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import esdip.pickapps.CustomWallpapersListFragment.OnListFragmentInteractionListener;
import esdip.pickapps.packages.widgets;

public class CustomWallpapersViewAdapter extends RecyclerView.Adapter<CustomWallpapersViewAdapter.ViewHolder> {

    private static final String DEBUG_TAG = "DEBUG";
    private static final boolean debug = false;

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

    public CustomWallpapersViewAdapter(Cursor data, String current_package, OnListFragmentInteractionListener listener) {
        mValues = data;
        active_package = current_package;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);*/
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_row_layout, parent, false);
        return new ViewHolder(view);
    }


    public void createView(final ViewHolder holder, boolean override){


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
         //createView(holder, false);
        if(debug)Log.i(DEBUG_TAG, "CustomWallpapersViewAdapter onBindViewHolder");

        if (mValues != null && mValues.moveToPosition(position)) {

           // while (!mValues.isAfterLast()) {

                if (mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_CUSTOM_WALLPAPER_NAME)) != null) {

                    String name = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_CUSTOM_WALLPAPER_NAME));
                    String behavior = mValues.getString(mValues.getColumnIndex(DBManager.COLUMN_CUSTOM_WALLPAPER_BEHAVIOR));

                    holder.name.setText(name);
                    holder.behavior.setText(behavior);

                    if(name.equals(active_package)){

                        holder.imageView.setImageResource(R.drawable.on);

                    }else{

                        holder.imageView.setImageResource(R.drawable.off);
                    }

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mListener) {

                                clicked_package_name = String.valueOf(holder.name.getText());
                                clicked_package_behavior = String.valueOf(holder.behavior.getText());

                                mListener.onCustomWallpapersListFragmentInteraction(clicked_package_name, clicked_package_behavior);
                            }
                        }
                    });
                }
             //   mValues.moveToNext();
           // }
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

                    mListener.onCustomWallpapersListFragmentInteraction(clicked_package_name, clicked_package_behavior);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(debug)Log.i(DEBUG_TAG, "CustomWallpapersViewAdapter getItemCount: " + String.valueOf(mValues.getCount()));
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




}
