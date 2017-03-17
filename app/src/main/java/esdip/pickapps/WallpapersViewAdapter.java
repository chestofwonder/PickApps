package esdip.pickapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import esdip.pickapps.WallpapersFragment.OnListFragmentInteractionListener;
import esdip.pickapps.packages.wallpapers;

public class WallpapersViewAdapter extends RecyclerView.Adapter<WallpapersViewAdapter.ViewHolder> {

    private static final String DEBUG_TAG = "DEBUG";

    private final List<wallpapers.wallpaper> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final String active_package;
    private Context context;
    SharedPreferences settings;
   /* public DownloadsViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }*/

    public WallpapersViewAdapter(List<wallpapers.wallpaper> data, String current_package, OnListFragmentInteractionListener listener) {
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

    // TODO: http://stackoverflow.com/questions/16346545/apply-many-color-filters-to-the-same-drawable

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final wallpapers.wallpaper item = mValues.get(position);

      //  for ( final wallpapers.wallpaper item  : mValues) {
            holder.textView.setText(item.package_uri);

            if(isInstalled(item.package_uri, context)){

                holder.imageView.setImageResource(item.thumbnail);
                holder.imageView.getDrawable().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY );

            }else{
                holder.imageView.setImageResource(item.thumbnail);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            mListener.onWallpapersFragmentInteraction(item);
                        }
                    }
                });
            }
       // }
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
        //public final  TextView textActiveView;
        public final  ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
           // mIdView = (TextView) view.findViewById(R.id.id);
            //mContentView = (TextView) view.findViewById(R.id.content);

            textView = (TextView) view.findViewById(R.id.package_name);
           // textActiveView = (TextView) view.findViewById(R.id.package_active);
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
