package esdip.pickapps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.*;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import esdip.pickapps.WidgetsFragment.OnListFragmentInteractionListener;
import esdip.pickapps.packages.widgets;

public class WidgetsViewAdapter extends RecyclerView.Adapter<WidgetsViewAdapter.ViewHolder> {

    private static final String DEBUG_TAG = "DEBUG";

    private final List<widgets.widget> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final String active_package;
    private Context context;
    SharedPreferences settings;
   /* public DownloadsViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }*/

    public WidgetsViewAdapter(List<widgets.widget> data, String current_package, OnListFragmentInteractionListener listener) {
        mValues = data;
        active_package = current_package;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);*/
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final widgets.widget item = mValues.get(position);

      //  for ( final widgets.widget item  : mValues) {
            holder.textView.setText(item.package_uri);

            if(isInstalled(item.package_uri, context)){

                holder.imageView.setImageResource(item.thumbnail);
                holder.imageView.getDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);

            }else{

                holder.imageView.setImageResource(item.thumbnail);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onWidgetsFragmentInteraction(item);
                        }
                    }
                });
            }
      //  }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
      //  public final TextView mIdView;
       // public final TextView mContentView;
       // public int mItem;

        public final  TextView textView;
       // public final  TextView textActiveView;
        public final  ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
           // mIdView = (TextView) view.findViewById(R.id.id);
            //mContentView = (TextView) view.findViewById(R.id.content);

            textView = (TextView) view.findViewById(R.id.package_name);
            //textActiveView = (TextView) view.findViewById(R.id.package_active);
            imageView = (ImageView) view.findViewById(R.id.package_thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + textView.getText() + "'";
        }
    }

    static boolean isInstalled(String uri, Context context) {

        android.content.pm.PackageManager pm = context.getPackageManager();

        try {
            pm.getPackageInfo(uri, android.content.pm.PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
