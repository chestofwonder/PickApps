package esdip.pickapps;

import android.content.Context;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CustomWallpapersListFragment extends Fragment {

    private static final String DEBUG_TAG = "DEBUG";
    private static final Boolean debug = false;

    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/custom_wallpapers");
    private String active_package;
    private static SharedPreferences settings;
    private static Cursor items;

    private OnListFragmentInteractionListener mListener;

    public CustomWallpapersListFragment() {
        // Required empty public constructor
    }

    public static CustomWallpapersListFragment newInstance() {
        CustomWallpapersListFragment fragment = new CustomWallpapersListFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        active_package = settings.getString(getString(R.string.active_package), null);
        items = getCustomWallpapers();

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            //Context context = view.getContext();

            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setAdapter(new CustomWallpapersViewAdapter(items, active_package, mListener));
        }
        return view;

       // return inflater.inflate(R.layout.fragment_custom_wallpapers_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
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
        void onCustomWallpapersListFragmentInteraction(String item, String behavior);
    }

    private Cursor getCustomWallpapers() {

        if(debug)Log.i(DEBUG_TAG, "getCustomWallpapers");

        // Get all installed wallpapers
        CursorLoader cursorLoader = new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
        return cursorLoader.loadInBackground();
    }

}
 //TODO: añadir los números en las iamgenes de teatre wallpaper