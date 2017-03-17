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

// Fragment to display "DOWNLOADS" section on tabs

public class DownloadsFragment extends Fragment {


   // private static final String ARG_SECTION_NUMBER = "section number";

    private OnListFragmentInteractionListener mListener;

    private static final String DEBUG_TAG = "DEBUG";
    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/wallpapers");

    private static SharedPreferences settings;
    private static Cursor items;

    String active_package;

    public DownloadsFragment() {
    }


    @SuppressWarnings("unused")
    public static DownloadsFragment newInstance() {

        Log.i(DEBUG_TAG, "newInstance DownloadsFragment");

        DownloadsFragment fragment = new DownloadsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(DEBUG_TAG, "onCreate DownloadsFragment");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(DEBUG_TAG, "onCreateView DownloadsFragment");

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        active_package = settings.getString(getString(R.string.active_package), null);
        items = getInstalledPackages();

        Log.i(DEBUG_TAG, "items: " + String.valueOf(items.getCount()));

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
            recyclerView.setAdapter(new DownloadsViewAdapter(items, active_package, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {

        Log.i(DEBUG_TAG, "onAttach DownloadsFragment");

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

        Log.i(DEBUG_TAG, "onDetach DownloadsFragment");

        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onDownloadsFragmentInteraction(String item, String behavior);
    }

    private Cursor getInstalledPackages() {

        Log.i(DEBUG_TAG, "getInstalledPackages DownloadsFragment");

        // Get all installed wallpapers
        CursorLoader cursorLoader = new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
        return cursorLoader.loadInBackground();
    }
}
