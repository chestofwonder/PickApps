package esdip.pickapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import esdip.pickapps.packages.wallpapers;

// Fragment to display "WALLPAPERS" section on tabs

public class WallpapersFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private static final String DEBUG_TAG = "DEBUG";
    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/wallpapers");

    private static SharedPreferences settings;
    private static List<wallpapers.wallpaper> items;

    String active_package;

    public WallpapersFragment() {
    }


    @SuppressWarnings("unused")
    public static WallpapersFragment newInstance() {
        Log.i(DEBUG_TAG, "newInstance WallpapersFragment");

        WallpapersFragment fragment = new WallpapersFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(DEBUG_TAG, "onCreate WallpapersFragment");

        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        active_package = settings.getString(getString(R.string.active_package), null);
        //items = getInstalledPackages();
        items = wallpapers.getWallpapers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        //borrar_ContentViewsAdapter adapter = new borrar_ContentViewsAdapter(this, items);
        //setListAdapter(adapter);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;
          /*  if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }*/
            recyclerView.setAdapter(new WallpapersViewAdapter(items, active_package, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        Log.i(DEBUG_TAG, "onAttach WallpapersFragment");

        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onWallpapersFragmentInteraction(wallpapers.wallpaper item);
    }
}
