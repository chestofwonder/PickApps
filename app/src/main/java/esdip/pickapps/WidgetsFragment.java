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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import esdip.pickapps.packages.widgets;

// Fragment to display "WIDGETS" section on tabs

public class WidgetsFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    private static final String DEBUG_TAG = "DEBUG";
    private static final String PROVIDER_NAME = "pickapps.contentprovider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/wallpapers");

    private static SharedPreferences settings;
    private static List<widgets.widget> items;

    String active_package;

    public WidgetsFragment() {
    }


    @SuppressWarnings("unused")
    public static WidgetsFragment newInstance() {
        WidgetsFragment fragment = new WidgetsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        active_package = settings.getString(getString(R.string.active_package), null);
        //items = getInstalledPackages();
        items = widgets.getWidgets();
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
            recyclerView.setAdapter(new WidgetsViewAdapter(items, active_package, mListener));
        }
        return view;
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
        void onWidgetsFragmentInteraction(widgets.widget item);
    }
}
