package example.android.gakuseimeshi.activity.map;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;

import com.google.android.gms.maps.GoogleMap;

import example.android.gakuseimeshi.R;

/**
 * Created by riku on 2018/02/19.
 */

public class TransportationTab extends Fragment{
    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.transportation_tab, null);

        int icon_size = MapsActivity2.maps_view_width/15;
/*
        Bitmap walk_icon_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_type_button);
        walk_icon_bitmap = Bitmap.createScaledBitmap(walk_icon_bitmap, icon_size, icon_size, false);
        Bitmap car_icon_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map_type_button);
        car_icon_bitmap = Bitmap.createScaledBitmap(car_icon_bitmap, icon_size, icon_size, false);
*/
        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setText(""));
        tabLayout.addTab(tabLayout.newTab().setText(""));
        //tabLayout.getTabAt(0).setIcon(R.drawable.walk_icon);
        tabLayout.getTabAt(0).setCustomView(R.layout.walk_icon);
        tabLayout.getTabAt(1).setCustomView(R.layout.car_icon);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TransportationTab", "tab=" + tab.getPosition());
                MapsActivity2.walk_or_car(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.parseColor("#ee505050"));
    }
}