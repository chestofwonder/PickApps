package esdip.pickapps;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import esdip.pickapps.packages.DummyContent;

public class EditorWidgetsImageSelectorActivity extends AppCompatActivity implements imagepackageconfigFragment.OnListFragmentInteractionListener{

    // Fragment interaction
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_widgets_image_selector);

        fm = getSupportFragmentManager();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ft = fm.beginTransaction();
        ft.add(R.id.weather_placeholder, imagepackageconfigFragment.newInstance(1));
        ft.commit();
    }

    public void onListFragmentInteraction(DummyContent.DummyItem item){

    }
}
